package com.camping.admin.support;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class ApiHelper {

    private ApiHelper() {}

    public static Response makeAuthenticatedRequest(String method, String endpoint, Object body) {
        return makeRequest(method, endpoint, body, CommonContext.adminToken);
    }

    public static Response makeUnauthenticatedRequest(String method, String endpoint, Object body) {
        return makeRequest(method, endpoint, body, null);
    }

    public static Response makeRequest(String method, String endpoint, Object body, String token) {
        var requestSpec = prepareRequestSpec(token, body);
        Response response = executeHttpMethod(method, endpoint, requestSpec);
        CommonContext.lastResponse = response;
        return response;
    }

    private static RequestSpecification prepareRequestSpec(String token, Object body) {
        var requestSpec = given().spec(CommonContext.requestSpec);

        if (token != null) {
            requestSpec = requestSpec.header("Authorization", "Bearer " + token);
        }

        if (body != null) {
            requestSpec = requestSpec.body(body);
        }
        
        return requestSpec;
    }

    private static Response executeHttpMethod(String method, String endpoint, RequestSpecification requestSpec) {
        switch (method.toUpperCase()) {
            case "GET":
                return requestSpec.get(endpoint);
            case "POST":
                return requestSpec.post(endpoint);
            case "PATCH":
                return requestSpec.patch(endpoint);
            case "PUT":
                return requestSpec.put(endpoint);
            case "DELETE":
                return requestSpec.delete(endpoint);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    public static Response updateReservationStatus(Long reservationId, String status) {
        return updateReservationStatus(reservationId, status, CommonContext.adminToken);
    }

    public static Response updateReservationStatus(Long reservationId, String status, String token) {
        Map<String, String> body = Map.of("status", status);
        return makeRequest("PATCH", "/admin/reservations/" + reservationId + "/status", body, token);
    }
}
