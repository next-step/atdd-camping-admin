package com.camping.admin.utils;

import java.util.Map;

public final class TestDataBuilder {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PRODUCT_TYPE = "productType";

    private TestDataBuilder() {
    }

    public static Map<String, String> adminCredentials() {
        return Map.of(
                KEY_USERNAME, "admin",
                KEY_PASSWORD, "admin123"
        );
    }

    public static Map<String, String> reservationStatusPayload(String status) {
        return Map.of(KEY_STATUS, status);
    }

    public static Map<String, String> productTypePayload(String productType) {
        return Map.of(KEY_PRODUCT_TYPE, productType);
    }
}
