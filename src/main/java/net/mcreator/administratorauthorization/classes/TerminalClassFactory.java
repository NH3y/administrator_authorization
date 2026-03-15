package net.mcreator.administratorauthorization.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.classes.interceptor.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.MethodDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class TerminalClassFactory {
    private final Map<Class<?>, Class<?>> swapMap;
    public final Map<Class<?>, String> interceptorName;
    private final Logger logger;

    private static final Path PATH = Paths.get("proxy.json");
    private static TerminalClassFactory instance;

    public static TerminalClassFactory getInstance() {
        if (instance == null) {
            instance = new TerminalClassFactory();
            AdministratorAuthorizationMod.LOGGER
                    .info("Creating TerminalClassFactory: {}", instance.hashCode());

        }
        return instance;
    }

    private TerminalClassFactory() {
        swapMap = new HashMap<>();
        interceptorName = new HashMap<>();
        logger = LoggerFactory.getLogger(TerminalClassFactory.class);
    }

    @SuppressWarnings("unchecked")
    public boolean createFor(Class<?> clazz, Settings settings) {
        if (clazz.getName().startsWith("com.orangeandy2007.gmail.interceptor.")) {
            logger.info("Skipping creation for interceptor class");
            return false;
        }
        if (hasProxyFor(clazz)) {
            logger.info("Skipping creation for {}", clazz.getName());
            return false;
        }
        Constructor<?> ctor = getEntityConstructor(clazz);
        if (ctor == null) {
            logger.error("Cannot find entity constructor for {}", clazz);
            return false;
        }

        String nameInterceptor = "interceptor";
        String nameHaltingValve = "haltingValve";
        DynamicType.Builder<?> builder = new ByteBuddy()
                .subclass(clazz)
                .name("com.orangeandy2007.gmail.interceptor." + clazz.getSimpleName() + "Interceptor")
                .implement(InterceptorAware.class)
                .defineField(nameInterceptor, Interceptor.class, Visibility.PUBLIC)
                .defineField(nameHaltingValve, HaltingValve.class, Visibility.PUBLIC)
                .defineConstructor(Visibility.PUBLIC)
                .withParameters(EntityType.class, Level.class, Interceptor.class)
                .intercept(
                        MethodCall
                                .invoke(ctor)
                                .withArgument(0, 1)
                                .andThen(FieldAccessor.ofField(nameInterceptor).setsArgumentAt(2))
                )
                .constructor(takesArguments(EntityType.class, Level.class))
                .intercept(
                        SuperMethodCall.INSTANCE
                                .andThen(FieldAccessor.ofField(nameInterceptor).setsValue(new CommonInterceptor()))
                )
                .method(named("getInterceptor"))
                .intercept(FieldAccessor.ofField(nameInterceptor))
                .method(named("setInterceptor"))
                .intercept(FieldAccessor.ofField(nameInterceptor))
                .method(named("getHaltingValve"))
                .intercept(FieldAccessor.ofField(nameHaltingValve))
                .method(named("setHaltingValve"))
                .intercept(FieldAccessor.ofField(nameHaltingValve))
                .method(
                        not(isConstructor())
                                .and(not(isAbstract()))
                )
                .intercept(MethodDelegation.to(HaltingValve.class));
        for (ElementMatcher<?> method : settings.methods) {
            builder = builder
                    .method((ElementMatcher<? super MethodDescription>) method)
                    .intercept(
                            MethodDelegation.to(InterceptionRouter.class)
                    );
        }
        try (DynamicType.Unloaded<?> unloaded = builder.make()) {
            //unloaded.saveIn(new File(clazz.getSimpleName()));

            Class<?> loaded = unloaded.load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
            swapMap.put(clazz, loaded);
            interceptorName.put(clazz, nameInterceptor);
            logger.info("create proxy for class:{}", clazz.getName());
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
        return true;
    }

    private Constructor<?> getEntityConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(ctor -> {
                    Class<?>[] parameterTypes = ctor.getParameterTypes();
                    return parameterTypes.length == 2 && parameterTypes[0] == EntityType.class && parameterTypes[1] == Level.class;
                })
                .findFirst()
                .orElse(null);
    }

    public boolean hasProxyFor(Class<?> clazz) {
        return swapMap.containsKey(clazz);
    }

    public <T extends Entity> T createProxy(T entity, ServerLevel level) {
        Class<? extends T> proxyClass = getProxyClass(entity);

        T proxy = null;
        try {
            Constructor<? extends T> ctor = proxyClass.getDeclaredConstructor(EntityType.class, Level.class, Interceptor.class);
            proxy = ctor.newInstance(entity.getType(), level, new CommonInterceptor());
        } catch (NoSuchMethodException e) {
            logger.error("Constructor not found for {}", proxyClass.getName());
        }catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            logger.error("Proxy instantiation failed — real cause:", cause);

            // Also print the full chain
            Throwable t = e;
            int depth = 0;
            while (t != null) {
                logger.error("  [{}] {}: {}", depth++, t.getClass().getName(), t.getMessage());
                t = t.getCause();
            }
            throw new RuntimeException("Proxy instantiation failed", cause);
        } catch (InstantiationException e) {
            logger.error("possible corrupted proxy class", e);
        } catch (IllegalAccessException e) {
            logger.warn("unexpected IllegalAccessException", e);
        }

        if (proxy == null) {
            return entity;
        }

        // 2. Copy UUID so identity checks pass
        proxy.setUUID(entity.getUUID());

        // 3. Copy NBT state — position, health, inventory, capabilities, everything
        CompoundTag tag = new CompoundTag();
        entity.save(tag);
        proxy.load(tag);

        // 4. Copy capabilities manually (Forge doesn't serialize all caps via NBT)
        // use your existing ReflectionUtils or direct cap transfer here
        CompoundTag nbt = proxy.serializeNBT();
        proxy.deserializeNBT(nbt);

        logger.info("create proxy for entity {}", proxy.getId());
        return proxy;
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> getProxyClass(T entity) {
        Class<?> aClass = entity.getClass();
        Class<?> swapClass = swapMap.get(aClass);
        return (Class<? extends T>) swapClass;
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(PATH, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))) {
            String json = gson.toJson(swapMap.keySet().stream().map(Class::getName).collect(Collectors.toSet()));
            writer.jsonValue(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (Files.notExists(PATH)) return ;
        try (JsonReader reader = new JsonReader(Files.newBufferedReader(PATH))) {
            TypeToken<Set<String>> collectionType = new TypeToken<>() {};
            Set<Class<?>> set = gson.fromJson(reader, collectionType).stream().map(name -> {
                try {
                    return Class.forName(name);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toSet());
            set.forEach(entityClass -> createFor(entityClass, Settings.NULL));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Settings {
        public static final Settings DEFAULT = new Settings(Set.of(
                ElementMatchers.returns(float.class)
                        .and(takesNoArguments())
                        .and(isDeclaredBy(LivingEntity.class))
                        .and(isPublic())
                        .and(isMethod())
                        .and(not(isStatic()))
                        .and(namedOneOf("getHealth", "m_21223_")),
                ElementMatchers.takesArguments(float.class)
                        .and(isDeclaredBy(LivingEntity.class))
                        .and(isPublic())
                        .and(isMethod())
                        .and(not(isStatic()))
                        .and(namedOneOf("setHealth", "m_21153")),
                ElementMatchers.returns(boolean.class)
                        .and(takesArguments(DamageSource.class, float.class))
                        //.and(isDeclaredBy(Entity.class))
                        .and(isPublic())
                        .and(isMethod())
                        .and(not(isStatic()))
                        .and(namedOneOf("hurt", "m_6469_")),
                ElementMatchers.returns(boolean.class)
                        .and(takesNoArguments())
                        .and(isDeclaredBy(LivingEntity.class))
                        .and(isPublic())
                        .and(isMethod())
                        .and(not(isStatic()))
                        .and(namedOneOf("isDeadOrDying", "m_21224_"))
        ));
        public static final Settings NULL = new Settings(Set.of());

        private final Set<ElementMatcher<?>> methods;
        private Settings(Set<ElementMatcher<?>> methodNames) {
            this.methods = methodNames;
        }

        public static SettingBuilder builder() {
            return new SettingBuilder();
        }
    }

    public static class SettingBuilder {
        private final Set<ElementMatcher<?>> methods;
        private SettingBuilder() {
            this.methods = new HashSet<>();
        }

        public SettingBuilder withMethod(ElementMatcher<?> method) {
            this.methods.add(method);
            return this;
        }

        public Settings build() {
            return new Settings(this.methods);
        }
    }
}
