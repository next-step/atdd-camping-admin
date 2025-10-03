package com.camping.admin.helpers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class AuthHelper {
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    public static String getAccessToken() {
        return ContextHelper.get(ACCESS_TOKEN_KEY, String.class);
    }

    public static void setTokens(String accessToken, String refreshToken) {
        ContextHelper.set(ACCESS_TOKEN_KEY, accessToken);
        if (refreshToken != null) {
            ContextHelper.set(REFRESH_TOKEN_KEY, refreshToken);
        }
    }

    public static void clearTokens() {
        ContextHelper.remove(ACCESS_TOKEN_KEY);
        ContextHelper.remove(REFRESH_TOKEN_KEY);
    }

    public static void performAdminLogin() {
        performLogin("admin", "admin123");
    }

    public static void performLogin(String username, String password) {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().response();

        String accessToken = response.jsonPath().getString("accessToken");
        String refreshToken = response.jsonPath().getString("refreshToken");

        setTokens(accessToken, refreshToken);
    }

    public static boolean isAuthenticated() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }

    public static void ensureAuthenticated() {
        if (!isAuthenticated()) {
            performAdminLogin();
        }
    }

}
