package com.camping.admin.hooks;

import com.camping.admin.support.Context;
import com.camping.admin.support.Role;
import io.cucumber.java.BeforeAll;
import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.Map;

public class TokenHook {
    public static Context context;

    @BeforeAll
    public static void initTokens() {
        context = new Context();

        login(Role.관리자);
    }

    public static void login(Role role) {
        String token = RestAssured.given()
                .contentType("application/json")
                .body(makeLoginRequest(role))
                .when().post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");

        context.setToken(role, token);
    }

    private static Map<String, String> makeLoginRequest(Role role) {
        Map<String, String> loginRequest = new HashMap<>();
        if (role.isAdmin()) {
            loginRequest.put("username", "admin");
            loginRequest.put("password", "admin123");
            return loginRequest;
        }
        loginRequest.put("username", "user");
        loginRequest.put("password", "user123");
        return loginRequest;
    }
}
