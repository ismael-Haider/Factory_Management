package inventory.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final ConcurrentHashMap<Class<?>, AtomicInteger> counters = new ConcurrentHashMap<>();

    public static int generateId(Class<?> clazz) {
        counters.putIfAbsent(clazz, new AtomicInteger(0));
        return counters.get(clazz).incrementAndGet();
    }
}