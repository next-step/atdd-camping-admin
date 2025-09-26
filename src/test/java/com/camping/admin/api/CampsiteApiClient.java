package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class CampsiteApiClient {
    public static ExtractableResponse<Response> sendCreateCampsite(
            String accessToken,
            Map<String, Object> requestBody
    ) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(requestBody)
                .when().log().all()
                .post("/admin/campsites")
                .then().log().all()
                .extract();
    }
}
