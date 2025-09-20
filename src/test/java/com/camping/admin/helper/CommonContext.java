package com.camping.admin.helper;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class CommonContext {
    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
                .setAccept(JSON)
                .setContentType(JSON)
                .log(LogDetail.ALL)
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
                .jsonPath()
                .getString("accessToken");
    }
}
