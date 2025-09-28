package com.camping.admin.steps;

import com.camping.admin.context.ScenarioContext;
import com.camping.admin.utils.AcceptanceTest;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class AuthSteps extends AcceptanceTest {

    public void 관리자가_로그인했다() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().response();

        String token = response.jsonPath().getString("accessToken");
        ScenarioContext.get().set("accessToken", token);
    }
}
