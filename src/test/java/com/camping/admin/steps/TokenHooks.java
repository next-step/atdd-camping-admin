package com.camping.admin.steps;

import com.camping.admin.helper.CommonContext;
import io.cucumber.java.BeforeAll;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

/**
 * Token 초기화
 * cucumber의 BeforeAll을 사용해야함. junit BeforeAll을 사용하지 않도록 주의
 */
@SuppressWarnings("unused")
public class TokenHooks {

    @BeforeAll
    public static void initAdminToken() {
        CommonContext.adminToken = getAdminToken();
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
