package com.camping.admin.helper;

import static com.camping.admin.context.CommonContext.getRequestSpec;
import static io.restassured.RestAssured.given;

import java.util.Map;

public class TestApiHelper {

    public static String authenticateAndGetToken() {
        return given()
            .spec(getRequestSpec())
            .contentType("application/json")
            .body(Map.of("username", "admin", "password", "admin123"))
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .cookie("AUTH_TOKEN");
    }

    public static void cleanupDatabase(String token) {
        given().spec(getRequestSpec())
            .header("Authorization", "Bearer " + token)
            .when()
            .post("/api/admin/reset-db")
            .then()
            .statusCode(200);
    }
}
