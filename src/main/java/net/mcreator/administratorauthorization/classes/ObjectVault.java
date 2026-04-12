package net.mcreator.administratorauthorization.classes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectVault {
    private static final Map<Integer, ObjectEntry> objects = new ConcurrentHashMap<>();
    private static final Map<Object, Integer> lookup = new ConcurrentHashMap<>();
    private static final AtomicInteger nextId = new AtomicInteger(0);
    private static final AtomicInteger biggestId = new AtomicInteger(0);
    private static final int EXPECT_REMAIN = 1000;
    private static final int SPACES = 10;

    public static int storeObject(Object object) {
        if (lookup.containsKey(object)) {
            Integer key = lookup.get(object);
            objects.computeIfPresent(key, (id, entry) -> entry.addReference());
            return key;
        } else {
            int id = nextId.getAndIncrement();
            objects.put(id, new ObjectEntry(object));
            lookup.put(object, id);
            return id;
        }
    }

    public static Object getObject(int id) {
        ObjectEntry entry = objects.get(id);
        if (entry.removeReference()) {
            lookup.remove(entry.object);
            objects.remove(id);
            if (id > biggestId.get()) {
                biggestId.set(id);
            }
        }
        return entry.object;
    }

    public static void resetId() {
        if (nextId.getPlain() > 4E7 || biggestId.getPlain() > EXPECT_REMAIN * (SPACES + 1)) {
            nextId.set(0);
            biggestId.set(0);
        }
    }

    private static class ObjectEntry {
        AtomicInteger referenceCount = new AtomicInteger(1);
        final Object object;

        private ObjectEntry(Object object) {
            this.object = object;
        }

        ObjectEntry addReference() {
            referenceCount.incrementAndGet();
            return this;
        }

        boolean removeReference() {
            return referenceCount.decrementAndGet() == 0;
        }
    }
}
