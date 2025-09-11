package com.camping.admin.steps;

import io.cucumber.java.BeforeStep;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    @BeforeStep
    public void initAccessToken() {
        log.info("로그인 시도중...");
        Map<String, String> params = Map.of("username", "admin", "password", "admin123");

        ExtractableResponse<Response> response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(params)
                .when()
                .post("http://localhost:8081/auth/login")
                .then()
                .statusCode(200)
                .extract();

        String accessToken = response.jsonPath().get("accessToken");
        StepContext.setAccessToken(accessToken);
        log.info("로그인 완료");
    }
}
