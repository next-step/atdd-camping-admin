package com.camping.admin.http;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

public final class RequestSpecFactory {
    private RequestSpecFactory() {
    }

    public static RequestSpecification base() {
        String baseUri = System.getProperty("BASE_URI", "http://localhost:8080");
        return new RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setContentType("application/json")
            .addFilter(new ErrorLoggingFilter())
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .build().log().all();
    }

    public static RequestSpecification withBearer(RequestSpecification base, String token) {
        return new RequestSpecBuilder()
            .addRequestSpecification(base)
            .addHeader("Authorization", "Bearer " + token)
            .build().log().all();
    }
}
