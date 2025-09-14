package com.camping.admin.client;

import com.camping.admin.CommonContext;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthClient {
    public String login(String username, String password) {
        Response response = given().log().all()
            .spec(CommonContext.baseSpec)
            .when().log().all()
            .body(Map.of("username", username, "password", password))
            .post("auth/login");

        response.then().statusCode(200);
        return response.body().jsonPath().getString("accessToken");
    }
}
