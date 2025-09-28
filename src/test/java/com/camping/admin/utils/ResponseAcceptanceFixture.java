package com.camping.admin.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

public final class ResponseAcceptanceFixture {

    private ResponseAcceptanceFixture() {
    }

    public static ExtractableResponse<Response> extract(Response response, HttpStatus expectedStatus) {
        return response.then()
                .log().all()
                .statusCode(expectedStatus.value())
                .extract();
    }
}
