package com.camping.admin.support;

import com.camping.admin.fixture.TestConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestAssuredConfig {

    private static RequestSpecification requestSpec;

    public static RequestSpecification getRequestSpec() {
        if (requestSpec == null) {
            requestSpec = createRequestSpec();
        }
        return requestSpec;
    }

    public static RequestSpecification createRequestSpec() {
        return new RequestSpecBuilder()
            .setBaseUri(TestConfig.BASE_URL)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    }

    public static RequestSpecification createAuthenticatedSpec(String token) {
        return new RequestSpecBuilder()
            .addRequestSpecification(getRequestSpec())
            .addHeader("Authorization", "Bearer " + token)
            .build();
    }

    private RestAssuredConfig() {}
}
