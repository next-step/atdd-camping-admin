package com.camping.admin.steps;

import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class CommonContext {

    private static String adminToken;

    static {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static String getAdminToken() {
        if (adminToken == null) {
            adminToken = generateAdminToken();
        }
        return adminToken;
    }

    private static String generateAdminToken() {
        return RestAssured
            .given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
            .when()
                .post("/auth/login")
            .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .cookie("AUTH_TOKEN");
    }
}
