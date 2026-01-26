package com.camping.admin.common;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class TestContext {

    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(HashMap::new);

    // === 기본 메서드 ===

    public static void set(String key, Object value) {
        context.get().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> type) {
        return (T) context.get().get(key);
    }

    public static void clear() {
        context.get().clear();
    }

    // === 편의 메서드: Admin Token ===

    private static final String ADMIN_TOKEN = "adminToken";

    public static void setAdminToken(String token) {
        set(ADMIN_TOKEN, token);
    }

    public static String getAdminToken() {
        return get(ADMIN_TOKEN, String.class);
    }

    // === 편의 메서드: Last Response ===

    private static final String LAST_RESPONSE = "lastResponse";

    public static void setLastResponse(Response response) {
        set(LAST_RESPONSE, response);
    }

    public static Response getLastResponse() {
        return get(LAST_RESPONSE, Response.class);
    }

    // === 편의 메서드: Reservation ID ===

    private static final String RESERVATION_ID = "reservationId";

    public static void setReservationId(Long id) {
        set(RESERVATION_ID, id);
    }

    public static Long getReservationId() {
        return get(RESERVATION_ID, Long.class);
    }

    // === 편의 메서드: Product ID ===

    private static final String PRODUCT_ID = "productId";

    public static void setProductId(Long id) {
        set(PRODUCT_ID, id);
    }

    public static Long getProductId() {
        return get(PRODUCT_ID, Long.class);
    }
}
