package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class ReservationApi {

    public static Response 예약_상태를_변경한다(String token, Long reservationId, String status) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("status", status))
                .patch("/admin/reservations/" + reservationId + "/status");
    }

    public static Response 예약을_취소한다(String token, Long reservationId) {
        return 예약_상태를_변경한다(token, reservationId, "CANCELLED");
    }
}
