package com.camping.admin.support.api;

import static io.restassured.RestAssured.given;

import io.cucumber.java.an.E;
import io.restassured.response.ExtractableResponse;
import java.util.Map;

public class ReservationApi {

    public ExtractableResponse<?> find(String authToken, long id) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .when().get("/admin/reservations/{id}", id)
                .then().extract();
    }

    public ExtractableResponse<?> patchStatus(String authToken, long id, String to) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(Map.of("status", to))
                .when().patch("/admin/reservations/{id}/status", id)
                .then().extract();
    }
}
