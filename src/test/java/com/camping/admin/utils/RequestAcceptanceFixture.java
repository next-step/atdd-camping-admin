package com.camping.admin.utils;

import io.restassured.response.Response;

import java.util.Map;

public class RequestAcceptanceFixture {
    static final String RESERVATION_PATH = "/admin/reservations";

    public static Response patchReservationStatus(String adminToken, Long reservationId, Map<String, String> spec) {
        return RequestSpecFixture.authenticatedRequest(adminToken)
                .body(spec)
                .when()
                .patch(RESERVATION_PATH + "/{id}/status", reservationId);
    }
}
