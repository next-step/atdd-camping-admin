package com.camping.admin.support.api;

import static io.restassured.RestAssured.given;

import io.restassured.response.ExtractableResponse;
import java.util.Map;

public class AuthApi {

    public String loginAndGetCookieToken(String username, String password) {
        ExtractableResponse<?> res = given()
                .contentType("application/json")
                .body(Map.of("username", username, "password", password))
                .when().post("/auth/login")
                .then().extract();
        return res.cookie("AUTH_TOKEN");
    }

}
