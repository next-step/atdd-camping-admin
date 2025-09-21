package com.camping.admin.helper;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static com.camping.admin.helper.CommonContext.adminToken;
import static com.camping.admin.helper.CommonContext.requestSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class RequestSender {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    public static Response post(String url, Object request) {
        return commonRequest()
                .body(request)
                .when()
                .post(url);
    }

    private static RequestSpecification commonRequest() {
        return given().spec(requestSpec)
                .contentType(JSON)
                .header(AUTHORIZATION, BEARER + adminToken);
    }

    public static Response get(String url) {
        return commonRequest()
                .when()
                .get(url);
    }

    public static Response patch(String url, Object request) {
        return commonRequest()
                .body(request)
                .when()
                .patch(url);
    }

    public static Response patch(String url) {
        return commonRequest()
                .when()
                .patch(url);
    }
}
