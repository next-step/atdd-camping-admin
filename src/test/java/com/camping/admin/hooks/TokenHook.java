package com.camping.admin.hooks;

import com.camping.admin.support.TestContext;
import io.cucumber.java.BeforeAll;
import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.Map;

public class TokenHook {
    public static TestContext testContext;

    @BeforeAll
    public static void initTokens() {
        testContext = new TestContext();

        login("관리자");
    }

    public static void login(String role) {
        String token = RestAssured.given()
                .contentType("application/json")
                .body(makeLoginRequest(role))
                .when().post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");

        testContext.setTokenByRole(role, token);
    }

    private static Map<String, String> makeLoginRequest(String role) {
        Map<String, String> loginRequest = new HashMap<>();
        if (role.equals("관리자")) {
            loginRequest.put("username", "admin");
            loginRequest.put("password", "admin123");
            return loginRequest;
        }
        loginRequest.put("username", "user");
        loginRequest.put("password", "user123");
        return loginRequest;
    }
}
