package com.camping.admin.support;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecFactory {
    public static RequestSpecification create() {
        String baseUrl = System.getProperty("test.baseUrl",
                System.getenv().getOrDefault("TEST_BASE_URL", "http://localhost:8080"));
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .build();
    }
}
