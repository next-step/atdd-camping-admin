package com.camping.admin.support;

import com.camping.admin.fixture.TestConfig;
import io.restassured.RestAssured;

import java.util.Map;

public class AuthHelper {

    private static String adminToken;

    public static String getAdminToken() {
        if (adminToken == null) {
            adminToken = login(TestConfig.ADMIN_USERNAME, TestConfig.ADMIN_PASSWORD);
        }
        return adminToken;
    }

    public static String login(String username, String password) {
        return RestAssured.given()
            .spec(RestAssuredConfig.getRequestSpec())
            .body(Map.of(
                "username", username,
                "password", password
            ))
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .cookie("AUTH_TOKEN");
    }

    public static void clearToken() {
        adminToken = null;
    }

    private AuthHelper() {}
}
