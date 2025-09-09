package com.camping.admin.support;

import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class ApiHelper {

    private ApiHelper() {}

    public static Response makeAuthenticatedRequest(String method, String endpoint, Object body) {
        return makeRequest(method, endpoint, body, CommonContext.getAdminToken());
    }

    public static Response makeUnauthenticatedRequest(String method, String endpoint, Object body) {
        return makeRequest(method, endpoint, body, null);
    }

    public static Response makeRequest(String method, String endpoint, Object body, String token) {
        var requestSpec = given().spec(CommonContext.getRequestSpec());

        if (token != null) {
            requestSpec = requestSpec.header("Authorization", "Bearer " + token);
        }

        if (body != null) {
            requestSpec = requestSpec.body(body);
        }

        Response response;
        switch (method.toUpperCase()) {
            case "GET":
                response = requestSpec.get(endpoint);
                break;
            case "POST":
                response = requestSpec.post(endpoint);
                break;
            case "PATCH":
                response = requestSpec.patch(endpoint);
                break;
            case "PUT":
                response = requestSpec.put(endpoint);
                break;
            case "DELETE":
                response = requestSpec.delete(endpoint);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        CommonContext.setLastResponse(response);
        return response;
    }

    public static Response updateReservationStatus(Long reservationId, String status) {
        return updateReservationStatus(reservationId, status, CommonContext.getAdminToken());
    }

    public static Response updateReservationStatus(Long reservationId, String status, String token) {
        Map<String, String> body = Map.of("status", status);
        return makeRequest("PATCH", "/admin/reservations/" + reservationId + "/status", body, token);
    }
}
