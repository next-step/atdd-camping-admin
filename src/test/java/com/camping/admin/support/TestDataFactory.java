package com.camping.admin.support;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static String generateAdminToken() {
        return generateToken("admin", "admin123");
    }


    private static String generateToken(String username, String password) {
        RequestSpecification spec = RequestSpecFactory.create();

        Response response = given().spec(spec)
                .body(Map.of("username", username, "password", password))
                .when().post("/auth/login");

        if (response.getStatusCode() == 200) {
            return response.then().extract().cookie("AUTH_TOKEN");
        }
        throw new RuntimeException("Failed to generate token for " + username + ". Status: " + response.getStatusCode());
    }
}
