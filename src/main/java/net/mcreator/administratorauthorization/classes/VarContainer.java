package net.mcreator.administratorauthorization.classes;

import java.util.HashMap;
import java.util.Map;

public class VarContainer<T> {
    public static final Map<String, VarContainer<?>> byName = new HashMap<>();

    private T value;
    private final T initialValue;
    private final String varName;
    private T stored;
    private Lock status = Lock.FULL_ACCESS;
    private T point;

    public VarContainer(T initialValue, String varName) {
        this.initialValue = initialValue;
        this.value = initialValue;
        this.stored = initialValue;
        this.varName = varName;
    }

    public void set(T newValue) {
        if (status.writable) {
            if (
                    status.equals(Lock.STOP_POINT) &&
                    newValue instanceof Number newNumber &&
                    point instanceof Number thePoint
            ) {
                value = newNumber.doubleValue() > thePoint.doubleValue() ? newValue : value;
            } else {
                value = newValue;
            }
        }
    }

    public T get() {
        if (status.readable) {
            return value;
        }
        return stored;
    }

    public void lock(String name) {
        try {
            Lock lock = Lock.valueOf(name);
            this.status = lock;
            if (lock.equals(Lock.STOP_POINT)) {
                this.point = this.value;
            }
            System.out.println("Var Container " + varName + " is currently " + lock.name());
            if (!status.readable) {
                this.stored = this.value;
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isDefault() {
        return initialValue.equals(value);
    }

    public void indexing() {
        byName.put(varName, this);
        System.out.println("Indexing " + varName + " for later use");
    }

    private enum Lock {
        READ_ONLY(true, false),
        FULL_ACCESS(true, true),
        NO_ACCESS(false, false),
        STOP_POINT(true, true);

        public final boolean readable;
        public final boolean writable;
        Lock(boolean canRead, boolean canWrite) {
            this.readable = canRead;
            this.writable = canWrite;
        }
    }
}
