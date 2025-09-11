package com.camping.admin.steps.hooks;

import static io.restassured.RestAssured.given;

import io.cucumber.java.Before;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

public class Hooks {
    public static RequestSpecification authenticatedRequest;

    @Before
    public void setUp() {
        authenticate();
        cleanupDatabase();
    }

    private void authenticate() {
        String adminToken = given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .cookie("AUTH_TOKEN");

        authenticatedRequest = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .accept("application/json");
    }

    private void cleanupDatabase() {
        given()
                .spec(authenticatedRequest)
                .when()
                .post("/api/admin/reset-db")
                .then()
                .statusCode(200);
    }


}
