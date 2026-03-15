package net.mcreator.administratorauthorization.classes;

import net.mcreator.administratorauthorization.errors.MultipleAccessException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtils {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);
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
            Field source = fields.get(0);
            source.setAccessible(true);
            try {
                return source.get(body);
            } catch (Throwable e) {
                return null;
            }
        }
        throw new MultipleAccessException("More than one field has been found");
    }

    public void setValue(String name, Object body, Object value) {
        ArrayList<Field> fields = getFieldsWithName(name);
        if (fields.size() == 1) {
            Field injector = fields.get(0);
            try {
                injector.setAccessible(true);
                injector.set(body, value);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }

    private @NotNull ArrayList<Field> getFieldsWithName(String name) {
        ArrayList<Field> fields = new ArrayList<>();
        for (Class<?> aClass : this.getAllClasses()) {
            try {
                fields.add(aClass.getDeclaredField(name));
            } catch (NoSuchFieldException e) {
                return new ArrayList<>();
            }
        }
        return fields;
    }

    public List<Object> unwrap(List<Field> fields, Object body) {
        List<Object> objects = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                objects.add(field.get(body));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return objects;
    }

    public ArrayList<Field> getAllFieldsWithType(Class<?> type) {
        ArrayList<Field> fields = new ArrayList<>();
        for (Class<?> aClass : this.getAllClasses()) {
            fields.addAll(Arrays.stream(aClass.getDeclaredFields()).filter(field -> type.isAssignableFrom(field.getType())).collect(Collectors.toSet()));
        }
        return fields;
    }

    public List<Method> getAllMethodsWithType(Class<?> type) {
        ArrayList<Method> methods = new ArrayList<>();
        for (Class<?> aClass : this.getAllClasses()) {
            methods.addAll(Arrays.stream(aClass.getDeclaredMethods()).filter(method -> type.isAssignableFrom(method.getReturnType())).collect(Collectors.toSet()));
        }
        return methods;
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
