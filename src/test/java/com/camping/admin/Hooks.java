package com.camping.admin;

import com.camping.admin.context.ScenarioContext;
import com.camping.admin.steps.AuthSteps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class Hooks {
    @Before
    public void beforeScenario() {
        ScenarioContext.clear();
            new AuthSteps().관리자가_로그인했다();

//        Response response = RestAssured
//                .given()
//                .contentType(ContentType.JSON)
//                .body(Map.of("username", "admin", "password", "admin123"))
//                .when()
//                .post("/auth/login")
//                .then()
//                .statusCode(200)
//                .extract().response();
//
//        String token = response.jsonPath().getString("accessToken");
//        ScenarioContext.get().set("accessToken", token);
    }

    @After
    public void afterScenario() {
        ScenarioContext.clear();
    }
}
