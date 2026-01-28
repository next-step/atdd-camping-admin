package com.camping.admin.common;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class TestContext {

    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(HashMap::new);

    public enum Key {
        ADMIN_TOKEN,
        LAST_RESPONSE,
        CAMPSITE_ID,
        PRODUCT_ID,
        RESERVATION_ID,
        RENTAL_RECORD_ID
    }

    public static void set(Key key, Object value) {
        context.get().put(key.name(), value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Key key, Class<T> type) {
        return (T) context.get().get(key.name());
    }

    public static void clear() {
        context.get().clear();
    }

    // ===== 편의 메서드 =====

    public static void setId(Key key, Long id) { set(key, id); }
    public static Long getId(Key key) { return get(key, Long.class); }

    public static String getAdminToken() { return get(Key.ADMIN_TOKEN, String.class); }
    public static void setAdminToken(String token) { set(Key.ADMIN_TOKEN, token); }

    public static Response getLastResponse() { return get(Key.LAST_RESPONSE, Response.class); }
    public static void setLastResponse(Response response) { set(Key.LAST_RESPONSE, response); }
}
