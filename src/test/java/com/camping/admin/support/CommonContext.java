package com.camping.admin.support;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public final class CommonContext {

    public static String adminToken;
    public static RequestSpecification requestSpec;
    public static Response lastResponse;
    public static Map<String, Object> lastParams;
}
