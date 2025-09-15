package com.camping.admin.support;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class CommonContext {

    public static String adminToken;
    public static RequestSpecification requestSpec;
    public static Response lastResponse;
}
