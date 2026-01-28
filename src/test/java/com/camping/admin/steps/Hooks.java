package com.camping.admin.steps;

import com.camping.admin.api.AuthApi;
import com.camping.admin.common.TestContext;
import io.cucumber.java.Before;
import io.restassured.RestAssured;

public class Hooks {

    @Before(order = 0)
    public void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Before(order = 1)
    public void setUp() {
        TestContext.clear();
    }

    @Before(order = 2)
    public void setupAdminToken() {
        String token = AuthApi.관리자_토큰을_발급한다();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("토큰 발급 실패 - 서버 상태 확인 필요");
        }
        TestContext.setAdminToken(token);
    }
}
