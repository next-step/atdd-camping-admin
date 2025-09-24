package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class ReservationApiClient {
    public static ExtractableResponse<Response> sendChangeStatus(
            String accessToken,
            Long reservationId,
            Map<String, Object> requestBody
    ) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(requestBody)
                .when().log().all()
                .patch("/admin/reservations/{id}/status", reservationId)
                .then().log().all()
                .extract();
    }
}
