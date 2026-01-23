package com.camping.admin.support;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthHelper {

    private static final String BASE_URI = "http://localhost:8080";
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin123";

    private String adminToken;

    public AuthHelper() {
        RestAssured.baseURI = BASE_URI;
        this.adminToken = loginAsAdmin();
    }

    private String loginAsAdmin() {
        return given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .cookie("AUTH_TOKEN");
    }

    public String getAdminToken() {
        return adminToken;
    }

    /**
     * 인증된 요청 스펙 반환
     */
    public RequestSpecification authorizedSpec() {
        return given()
                .contentType("application/json")
                .accept("application/json")
                .cookie("AUTH_TOKEN", adminToken);
    }
}
