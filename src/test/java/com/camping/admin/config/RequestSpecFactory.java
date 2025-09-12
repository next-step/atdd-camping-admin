package com.camping.admin.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecFactory {

    public static final String BASE_URL = "http://localhost:8080";

    public static RequestSpecification create() {
        String baseUrl = System.getProperty("test.baseUrl", System.getenv("TEST_BASE_URL"));
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = BASE_URL;
        }

        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }
}

