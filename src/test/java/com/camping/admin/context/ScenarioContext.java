package com.camping.admin.context;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    private static final ThreadLocal<ScenarioContext> THREAD_LOCAL =
            ThreadLocal.withInitial(ScenarioContext::new);

    public static ScenarioContext get() {
        return THREAD_LOCAL.get();
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }
    private final Map<String, Object> data = new HashMap<>();

    public void set(String key, Object value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        return (T) data.get(key);
    }
}
