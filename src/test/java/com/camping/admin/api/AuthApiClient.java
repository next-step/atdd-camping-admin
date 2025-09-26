package com.camping.admin.api;

import io.restassured.RestAssured;

import java.util.Map;

public class AuthApiClient {
    public static String getAccessToken() {
        return RestAssured
                .given()
                .log().all()
                .contentType("application/json")
                .body(Map.of(
                        "username", "admin",
                        "password", "admin123"
                ))
                .when()
                .log().all()
                .post("/auth/login")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .jsonPath().getString("accessToken");
    }
}
