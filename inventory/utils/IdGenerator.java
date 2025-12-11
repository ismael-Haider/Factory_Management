package inventory.utils;

import java.util.concurrent.ConcurrentHashMap;

public class IdGenerator {
    private static final ConcurrentHashMap<Class<?>, Integer> counters = new ConcurrentHashMap<>();

    public static int generateId(Class<?> clazz,Integer i) {
        counters.put(clazz,  i);
        return counters.get(clazz);
    }
}