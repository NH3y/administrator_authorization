package net.mcreator.administratorauthorization.security;

import net.mcreator.administratorauthorization.classes.Vault;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntryAnalyzer {
    private static final EntryAnalyzer instance = new EntryAnalyzer();
    private final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static final Function<? super Stream<StackWalker.StackFrame>, Integer> MIXIN_METHOD = stream -> {
        Set<Method> set = stream
                .map(StackWalker.StackFrame::getDeclaringClass)
                .filter(clazz ->
                        Arrays.stream(clazz.getDeclaredMethods()).anyMatch(method -> method.getName().contains("$")) ||
                        Arrays.stream(clazz.getDeclaredFields()).anyMatch(field -> field.getName().contains("$"))
                )
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods())
                        .filter(method ->
                                method.getName().contains("$") &&
                                !method.getName().contains("lambda") &&
                                !method.getName().contains("handler$") &&
                                !method.getName().contains("localvar") &&
                                !method.getName().contains("redirect") &&
                                !method.getName().contains("modify") &&
                                !method.getName().contains("modifyReceiver") &&
                                !method.getName().contains("wrapOperation") &&
                                !method.getName().contains("wrapWithCondition") &&
                                !method.getName().startsWith("administrator_authorization")
                        )
                )
                .collect(Collectors.toSet());
        return Vault.sortMixin(set);
    };

    public static EntryAnalyzer getInstance() {
        return instance;
    }

    public int mixinMethods() {
        return walker.walk(MIXIN_METHOD);
    }

    @SuppressWarnings("unused")
    public void print() {
        List<String> classes = new ArrayList<>();
        walker.forEach(frame -> classes.add(frame.getClassName()));
        classes.forEach(System.out::println);
    }

    @SuppressWarnings("unused")
    public boolean has(String className, String methodName) {
        return walker.walk(stream -> stream.anyMatch(stackFrame ->
                stackFrame.getClassName().equals(className) &&  stackFrame.getMethodName().equals(methodName)
        ));
    }

    public void recordHurtMethod() {
        if (Vault.EntityCallContext.isPresent()) {
            Entity entity = Vault.EntityCallContext.get();
            String name = entity.getClass().getName();
            if (name.startsWith("com.orangeandy2007.gmail.interceptor.") && name.endsWith("Interceptor")) return;
            Arrays.stream(entity.getClass().getMethods()).filter(method -> {
                if (method.getParameters().length == 2) {
                    return method.getParameters()[0].getType().isAssignableFrom(DamageSource.class) && method.getParameters()[1].getType().isAssignableFrom(float.class);
                }
                return false;
            }).findFirst().ifPresent(data -> Vault.damageMethods.add(
                    data
            ));
        }
    }
}
