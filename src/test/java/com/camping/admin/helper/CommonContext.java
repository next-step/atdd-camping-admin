package com.camping.admin.helper;

import com.camping.admin.dto.LoginRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class CommonContext {
    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
                .build();
    }

    public static String getAdminToken() {
        return given()
                .contentType(JSON)
                .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                .post("/auth/login")
                .then()
                .log().all()
                .extract()
                .cookie("AUTH_TOKEN");
    }
}
