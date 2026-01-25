package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.Map;

public class AuthApi {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    public static String 관리자_토큰을_발급한다() {
        return 관리자_토큰을_발급한다(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD);
    }

    public static String 관리자_토큰을_발급한다(String username, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .post("/auth/login")
                .jsonPath()
                .getString("accessToken");
    }
}
