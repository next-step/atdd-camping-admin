package com.camping.admin.helpers;

import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

public class ReservationTestHelper {

    public static Long getReservationId() {
        return ContextHelper.getReservationId();
    }

    public static Response getReservationsList() {
        return ApiHelper.givenAuthenticated()
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract().response();
    }

    public static List<Map<String, Object>> getReservationsFromResponse(Response response) {
        return response.jsonPath().getList("");
    }

    public static void setReservationId(Long reservationId) {
        ContextHelper.setReservationId(reservationId);
    }

    public static void setResponse(Response response) {
        ContextHelper.setResponse(response);
    }

    public static Response getLastResponse() {
        return ContextHelper.getLastResponse();
    }

    public static Response patchReservationStatus(Long reservationId, String status) {
        Map<String, Object> statusUpdate = Map.of("status", status);
        return ApiHelper.patch("/admin/reservations/" + reservationId + "/status", statusUpdate);
    }

    public static Response patchReservationStatusWithoutBody(Long reservationId) {
        return ApiHelper.patch("/admin/reservations/" + reservationId + "/status");
    }

    public static Long findReservationByStatus(String status) {
        Response response = getReservationsList();
        List<Map<String, Object>> reservations = getReservationsFromResponse(response);

        return reservations.stream()
                .filter(reservation -> status.equals(reservation.get("status")))
                .map(reservation -> Long.valueOf(reservation.get("id").toString()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No reservation found with status: " + status));
    }

    public static Long findOrCreateReservationByStatus(String status) {
        try {
            return findReservationByStatus(status);
        } catch (RuntimeException e) {
            return createReservationByChangingStatus(status);
        }
    }

    private static Long createReservationByChangingStatus(String targetStatus) {
        Response response = getReservationsList();
        List<Map<String, Object>> reservations = getReservationsFromResponse(response);

        if (reservations.isEmpty()) {
            throw new RuntimeException("No reservations available to modify");
        }

        Long reservationId = Long.valueOf(reservations.get(0).get("id").toString());

        Response updateResponse = patchReservationStatus(reservationId, targetStatus);
        if (updateResponse.getStatusCode() == 200) {
            return reservationId;
        } else {
            throw new RuntimeException("Failed to update reservation status to: " + targetStatus);
        }
    }
}
