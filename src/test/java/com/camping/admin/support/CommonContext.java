package com.camping.admin.support;

import io.restassured.specification.RequestSpecification;

public final class CommonContext {
    private static RequestSpecification requestSpec;
    private static String adminToken;
    private static String userToken;

    private CommonContext() {}

    public static RequestSpecification getRequestSpec() {
        return requestSpec;
    }

    public static void setRequestSpec(RequestSpecification spec) {
        requestSpec = spec;
    }

    public static String getAdminToken() {
        return adminToken;
    }

    public static void setAdminToken(String token) {
        adminToken = token;
    }

    public static String getUserToken() {
        return userToken;
    }

    public static void setUserToken(String token) {
        userToken = token;
    }
}
