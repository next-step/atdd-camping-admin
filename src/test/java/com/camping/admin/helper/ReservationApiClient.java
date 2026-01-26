package com.camping.admin.helper;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured;
import java.util.Map;

/**
 * 예약 도메인 API 클라이언트
 */
public class ReservationApiClient {

    // 테스트용 고정 예약 ID (data.sql 기준)
    public static final int CONFIRMED_RESERVATION_ID = 1001;  // 취소 가능한 예약 (CONFIRMED)
    public static final int PENDING_RESERVATION_ID = 1002;    // 취소 가능한 예약 (PENDING)
    public static final int CANCELLED_RESERVATION_ID = 1003;  // 이미 취소된 예약
    public static final int CHECKED_OUT_RESERVATION_ID = 1004; // 체크아웃된 예약

    private final RequestSpecification requestSpec;

    public ReservationApiClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    public Response getReservations() {
        return RestAssured.given()
            .spec(requestSpec)
            .when()
            .get("/admin/reservations");
    }

    public Response updateStatus(int reservationId, String status) {
        return RestAssured.given()
            .spec(requestSpec)
            .body(Map.of("status", status))
            .when()
            .patch("/admin/reservations/" + reservationId + "/status");
    }

    public Response updateStatusWithEmptyBody(int reservationId) {
        return RestAssured.given()
            .spec(requestSpec)
            .body(Map.of())
            .when()
            .patch("/admin/reservations/" + reservationId + "/status");
    }

    public String getReservationStatus(int reservationId) {
        Response response = getReservations();
        return response.jsonPath()
            .getString("find { it.id == " + reservationId + " }.status");
    }
}