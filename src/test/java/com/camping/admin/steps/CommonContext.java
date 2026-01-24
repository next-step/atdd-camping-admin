package com.camping.admin.steps;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class CommonContext {
    private static String adminToken;
    private static RequestSpecification requestSpec;

    public static void init(int port, String token) {
        adminToken = token;
        requestSpec = RestAssured.given()
            .baseUri("http://localhost")
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token);
    }

    public static String getAdminToken() {
        return adminToken;
    }

    public static RequestSpecification getRequestSpec() {
        return requestSpec;
    }

    public static void clear() {
        adminToken = null;
        requestSpec = null;
    }
}