package net.mcreator.administratorauthorization.classes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@FunctionalInterface
public interface Invoker {
    Object invoke(Object target, Method method, Object[] args);

    Invoker NULL_INVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target);
        } catch (InvocationTargetException | IllegalAccessException ignored) {
        }
        return returnValue;
    };

    Invoker AINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker BINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0], args[1]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker CINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0], args[1], args[2]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker DINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0],  args[1], args[2],  args[3]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker EINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0],  args[1], args[2], args[3], args[4]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker FINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0], args[1], args[2], args[3], args[4], args[5]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker GINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0],  args[1], args[2], args[3], args[4], args[5], args[6]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker HINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker IINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker JINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0],  args[1], args[2], args[3], args[4], args[5],  args[6], args[7], args[8], args[9]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    Invoker KINVOKER = (target, method, args) -> {
        Object returnValue = null;
        try {
            returnValue = method.invoke(target, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
        } catch (InvocationTargetException | IllegalAccessException ignores) {
        }
        return returnValue;
    };

    static Invoker getInvoker(int parameterCount) {
        return switch (parameterCount) {
            case 0 -> NULL_INVOKER;
            case 1 -> AINVOKER;
            case 2 -> BINVOKER;
            case 3 -> CINVOKER;
            case 4 -> DINVOKER;
            case 5 -> EINVOKER;
            case 6 -> FINVOKER;
            case 7 -> GINVOKER;
            case 8 -> HINVOKER;
            case 9 -> IINVOKER;
            case 10 -> JINVOKER;
            case 11 -> KINVOKER;

            default -> throw new IllegalStateException("Unexpected value: " + parameterCount);
        };
    }
}
