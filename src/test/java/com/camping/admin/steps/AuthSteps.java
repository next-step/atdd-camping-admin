package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import com.camping.admin.support.RequestSpecFactory;
import io.cucumber.java.en.Given;
import io.restassured.http.Cookies;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthSteps {
    @Given("관리자가 로그인을 했다")
    public void 관리자가로그인을했다() {
        Response res = given().spec(RequestSpecFactory.create())
                .body(Map.of("username","admin","password","admin123"))
                .post("/auth/login")
                .then().extract().response();

        String token = res.jsonPath().getString("accessToken");
        if (token == null || token.isBlank()) {
            token = res.getCookie("AUTH_TOKEN");
        }

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("관리자 토큰을 가져오지 못했습니다. 로그인 응답 본문: " + res.asString());
        }

        CommonContext.setAdminToken(token);
    }
}
