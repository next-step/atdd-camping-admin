package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.Map;

import static com.camping.admin.hooks.TokenHook.testContext;

public class AuthSteps {

    @Given("{string} 가 로그인 한다.")
    public void login(String role) {
        String token = RestAssured.given()
                .contentType("application/json")
                .body(makeLoginRequest(role))
                .when().post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");
                
        testContext.setTokenByRole(role, token);
    }

    private Map<String, String> makeLoginRequest(String role) {
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
