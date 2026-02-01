package com.camping.admin.helpers;

import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReservationApiHelper extends ApiClientBase {

    private static final String BASE_PATH = "/admin/reservations";

    public Response getList() {
        return authenticated().get(BASE_PATH);
    }

    public String getStatus(Long id) {
        var response = getList();

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get reservations: " + response.statusCode()
                + " - " + response.body().asString());
        }

        var reservations = response.jsonPath().<Map<String, Object>>getList("");

        return reservations.stream()
            .filter(r -> id.equals(((Number) r.get("id")).longValue()))
            .findFirst()
            .map(r -> (String) r.get("status"))
            .orElse(null);
    }

    public Response getRentals(Long reservationId) {
        return authenticated().get(BASE_PATH + "/" + reservationId + "/rentals");
    }

    public Response changeStatus(Long id, String status) {
        return authenticated()
            .body(Map.of("status", status))
            .patch(BASE_PATH + "/" + id + "/status");
    }
}
