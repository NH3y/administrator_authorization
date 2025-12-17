package net.mcreator.administratorauthorization.classes;

import net.mcreator.administratorauthorization.errors.MultipleAccessException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionUtils {
    private final Class<?> targetClass;
    private final boolean INDEPENDENT;

    public ReflectionUtils(Class<?> target) {
        if (target == null) {
            throw new IllegalArgumentException("Target class can't be null");
        }
        this.targetClass = target;
        this.INDEPENDENT = target.getSuperclass() == null;
    }

    public Object getValue(String name, Object body) {
        ArrayList<Field> fields = getFieldsWithName(name);
        if (fields.size() == 1) {
            Field source = fields.getFirst();
            source.setAccessible(true);
            try {
                return source.get(body);
            } catch (IllegalAccessException ignored) {

            }
        }
        throw new MultipleAccessException("More than one field has been found");
    }

    public void setValue(String name, Object body, Object value) {
        ArrayList<Field> fields = getFieldsWithName(name);
        if (fields.size() == 1) {
            Field injector = fields.getFirst();
            try {
                injector.setAccessible(true);
                injector.set(body, value);
            } catch (IllegalAccessException ignored) {

            }
        }
    }

    private @NotNull ArrayList<Field> getFieldsWithName(String name) {
        ArrayList<Field> fields = new ArrayList<>();
        for (Class<?> aClass : this.getAllClasses()) {
            try {
                fields.add(aClass.getDeclaredField(name));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return fields;
    }

    public ArrayList<Field> getAllFieldsWithType(Class<?> type) {
        ArrayList<Field> fields = new ArrayList<>();
        for (Class<?> aClass : this.getAllClasses()) {
            fields.addAll(Arrays.stream(aClass.getDeclaredFields()).filter(field -> type.isAssignableFrom(field.getType())).collect(Collectors.toSet()));
        }
        return fields;
    }

    public ArrayList<Field> getSuspiciousFields(String involve) {
        ArrayList<Field> fields = new ArrayList<>();
        for (Class<?> aClass : this.getAllClasses()) {
            fields.addAll(Arrays.stream(aClass.getDeclaredFields()).filter(field -> field.getName().toLowerCase().contains(involve)).collect(Collectors.toSet()));
        }
        return fields;
    }

    public ArrayList<Method> getSuspiciousMethod(String involve) {
        ArrayList<Method> methods = new ArrayList<>();
        for (Class<?> aClass : this.getAllClasses()) {
            methods.addAll(Arrays.stream(aClass.getDeclaredMethods()).filter(method -> method.getName().toLowerCase().contains(involve)).collect(Collectors.toSet()));
        }
        return methods;
    }

    public Set<Class<?>> getAllClasses() {
        Set<Class<?>> classes = new HashSet<>(16);
        if (this.INDEPENDENT) {
            classes.add(this.targetClass);
            return classes;
        }
        Class<?> classHolder = this.targetClass;
        do {
            classes.add(classHolder);
        } while ((classHolder = classHolder.getSuperclass()) != null);
        return classes;
    }
}
