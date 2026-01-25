package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthAPI {

    /**
     * 로그인 API 호출
     * POST /auth/login
     */
    public ExtractableResponse<Response> 로그인(String username, String password) {
        Map<String, String> loginRequest = Map.of(
                "username", username,
                "password", password
        );

        return RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(loginRequest)
                .when()
                    .post("/auth/login")
                .then()
                    .extract();
    }
}