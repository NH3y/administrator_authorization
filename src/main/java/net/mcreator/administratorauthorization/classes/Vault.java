package net.mcreator.administratorauthorization.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Vault {
    public static Data<Integer> healthId = new Data<>(false);
    public static SetData<Method> damageMethods = new SetData<>(new HashSet<>(), true);
    public static SetData<Method> mixinMethods = new SetData<>(new HashSet<>(), false);

    public static void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(Paths.get("damage_methods.json"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))) {
            String json = gson.toJson(damageMethods.getData().stream().map(method -> method.getDeclaringClass().getName() + ":" + method.getName()).collect(Collectors.toSet()));
            writer.jsonValue(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();


        Path path = Paths.get("damage_methods.json");
        if (Files.notExists(path)) return ;
        try (JsonReader reader = new JsonReader(Files.newBufferedReader(path))) {
            TypeToken<Set<String>> collectionType = new TypeToken<>() {};
            Set<Method> set = gson.fromJson(reader, collectionType).stream().map(name -> {
                try {
                    String[] split = name.split(":");
                    return Class.forName(split[0]).getMethod(split[1], DamageSource.class, float.class);
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toSet());
            damageMethods.setData(set);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Data<T> {
        T data;
        boolean shouldSave;

        public Data(boolean shouldSave) {
            this.shouldSave = shouldSave;
        }

        public Data(T data,  boolean shouldSave) {
            this.data = data;
            this.shouldSave = shouldSave;
        }

        public void setData(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static class SetData<R> extends Data<Set<R>> {
        public SetData(Set<R> data, boolean shouldSave) {
            super(data, shouldSave);
        }

        public boolean add(R data) {
            return this.data.add(data);
        }

        public int addAll(Set<R> data) {
            Set<R> collect = data.stream()
                    .filter(r -> !this.data.contains(r))
                    .collect(Collectors.toSet());
            this.data.addAll(collect);
            return collect.size();
        }
    }

    public static final class EntityCallContext {
        private static final ThreadLocal<Entity> CURRENT_ENTITY = new ThreadLocal<>();

        public static void push(Entity entity) {
            CURRENT_ENTITY.set(entity);
        }

        public static void clear() {
            CURRENT_ENTITY.remove();
        }

        public static Entity get() {
            return CURRENT_ENTITY.get();
        }

        public static boolean isPresent() {
            return CURRENT_ENTITY.get() != null;
        }

        public static boolean isPresent(Consumer<Entity> run) {
            if (isPresent()) {
                run.accept(CURRENT_ENTITY.get());
                return true;
            }
            return false;
        }
    }

    public static final class HurtByContext {
        private static final ThreadLocal<Context> CURRENT_ENTITY = new ThreadLocal<>();

        public static void push(Context entity) {
            CURRENT_ENTITY.set(entity);
        }

        public static void clear() {
            CURRENT_ENTITY.remove();
        }

        public static Context get() {
            return CURRENT_ENTITY.get();
        }

        public static boolean isPresent() {
            return CURRENT_ENTITY.get() != null;
        }

        public static boolean isPresent(Consumer<Context> run) {
            if (isPresent()) {
                run.accept(CURRENT_ENTITY.get());
                return true;
            }
            return false;
        }

        public record Context(DamageSource source, float damage) {
        }
    }

    public static final Map<String, Set<Method>> sortedMixinMethods = new HashMap<>();
    public static final Map<String, Set<String>> keyWordSorter = new HashMap<>();
    static {
        Set<String> forceKeyWords = new HashSet<>();
        forceKeyWords.add("forced");
        forceKeyWords.add("forcing");
        forceKeyWords.add("force");
        keyWordSorter.put("FORCE", forceKeyWords);
        Set<String> setAndGetKeyWords = new HashSet<>();
        setAndGetKeyWords.add("set");
        setAndGetKeyWords.add("get");
        setAndGetKeyWords.add("is");
        keyWordSorter.put("SET_GET", setAndGetKeyWords);
        Set<String> healthKeyWords = new HashSet<>();
        healthKeyWords.add("health");
        healthKeyWords.add("hurt");
        healthKeyWords.add("damage");
        keyWordSorter.put("HEALTH",  healthKeyWords);
        Set<String> techKeyWords = new HashSet<>();
        techKeyWords.add("inject");
        techKeyWords.add("transform");
        techKeyWords.add("intercept");
        techKeyWords.add("field");
        techKeyWords.add("method");
        techKeyWords.add("class");
        keyWordSorter.put("TECH", techKeyWords);
        Set<String> eventKeyWords = new HashSet<>();
        eventKeyWords.add("event");
        eventKeyWords.add("fire");
        eventKeyWords.add("publish");
        eventKeyWords.add("broadcast");
        eventKeyWords.add("subscribe");
        keyWordSorter.put("EVENT",  eventKeyWords);
    }

    public static Integer sortMixin(Set<Method> set) {
        Set<Method> toSort = new HashSet<>();
        set.forEach(method -> {
            if (!mixinMethods.getData().contains(method)) {
                toSort.add(method);
                mixinMethods.add(method);
            }
        });
        AtomicInteger counter = new AtomicInteger();
        toSort.forEach(method -> {
            boolean hasSorted = false;
            for (Map.Entry<String, Set<String>> entry : keyWordSorter.entrySet()) {
                for (String word : entry.getValue()) {
                    if (method.getName().toLowerCase().contains(word)) {
                        sortedMixinMethods.computeIfAbsent(entry.getKey(),k -> new HashSet<>()).add(method);
                        System.out.println(entry.getKey() + "\t" + method.getName() + "\t" + word);
                        hasSorted = true;
                        counter.getAndIncrement();
                        break;
                    }
                }
            }
            if (!hasSorted) {
                sortedMixinMethods.computeIfAbsent("UNSORTED", k -> new HashSet<>()).add(method);
            }
        });

        return counter.get();
    }

}