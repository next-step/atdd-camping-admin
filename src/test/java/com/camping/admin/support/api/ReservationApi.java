package com.camping.admin.support.api;

import static io.restassured.RestAssured.given;

import io.restassured.response.ExtractableResponse;
import java.util.Map;

public class ReservationApi {

    public ExtractableResponse<?> patchStatus(String authToken, long id, String to) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(Map.of("status", to))
                .when().patch("/admin/reservations/{id}/status", id)
                .then().extract();
    }
}
