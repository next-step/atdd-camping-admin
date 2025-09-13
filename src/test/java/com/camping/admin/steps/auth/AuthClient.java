package com.camping.admin.steps.auth;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import java.util.Map;

public class AuthClient {

    public static Response 로그인_요청을_한다(String username, String password) {
        return given()
            .body(Map.of(
                "username", username,
                "password", password
            ))
            .when()
            .post("/auth/login")
            .andReturn();
    }
}
