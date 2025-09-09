package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.restassured.RestAssured;

import java.util.Map;

public class AdminAuthSteps {
    private final TestContext testContext;

    public AdminAuthSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("관리자가 로그인 한다.")
    public void loginAdmin() {
        String adminToken = RestAssured.given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when().post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");
                
        testContext.setAdminToken(adminToken);
    }
}
