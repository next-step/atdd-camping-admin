package com.camping.admin.utils;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public final class RequestSpecFixture {

    private static final String BASE_URI = "http://localhost";
    private static final int PORT = 8080;
    private static final String AUTH_COOKIE_NAME = "AUTH_TOKEN";

    private static final RequestSpecification BASE_SPEC = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setPort(PORT)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();

    private RequestSpecFixture() {
    }

    public static RequestSpecification baseRequest() {
        return RestAssured.given().spec(BASE_SPEC);
    }

    public static RequestSpecification authenticatedRequest(String token) {
        return baseRequest()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    public static String fetchAdminToken() {
        Response response = baseRequest()
                .body(TestDataBuilder.adminCredentials())
                .when()
                .post("/auth/login");

        ExtractableResponse<Response> extracted = ResponseAcceptanceFixture.extract(response, HttpStatus.OK);
        return extracted.cookie(AUTH_COOKIE_NAME);
    }
}
