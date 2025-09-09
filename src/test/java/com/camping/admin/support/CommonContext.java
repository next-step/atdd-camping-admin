package com.camping.admin.support;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class CommonContext {
    private static RequestSpecification requestSpec;
    private static String adminToken;
    private static Response lastResponse;

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

    public static Response getLastResponse() {
        return lastResponse;
    }

    public static void setLastResponse(Response response) {
        lastResponse = response;
    }
}
