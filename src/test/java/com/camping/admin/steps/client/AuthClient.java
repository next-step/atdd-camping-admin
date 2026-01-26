package com.camping.admin.steps.client;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthClient {

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    public String 관리자_로그인을_한다() {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", username, "password", password))
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .cookie("AUTH_TOKEN");
    }
}
