package com.camping.admin.steps;

import com.camping.admin.api.AuthApi;
import com.camping.admin.common.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;

public class Hooks {

    @Before(order = 0)
    public void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Before(order = 1)
    public void setupAdminToken() {
        String token = AuthApi.관리자_토큰을_발급한다();
        TestContext.setAdminToken(token);
    }

    @After
    public void cleanup() {
        TestContext.clear();
    }
}
