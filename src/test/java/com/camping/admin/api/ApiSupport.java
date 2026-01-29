package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiSupport {


    public static RequestSpecification baseRequest() {
        return RestAssured.given()
                .contentType(ContentType.JSON);
    }

    public static RequestSpecification authRequest(String token) {
        return baseRequest()
                .header("Authorization", "Bearer " + token);
    }
}
