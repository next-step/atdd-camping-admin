package com.camping.admin.client;

import com.camping.admin.steps.TestContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReservationAdminClient {

    @Autowired
    private TestContext testContext;

    public void 예약_상태를_변경한다(String status) {
        예약_상태를_변경한다(Map.of("status", status));
    }

    public void 예약_상태를_변경한다(Map<String, String> requestBody) {
        var reservationId = testContext.getReservationId();
        예약_상태를_변경한다(reservationId, requestBody);
    }

    public void 예약_상태를_변경한다(long reservationId, Map<String, String> requestBody) {
        var authToken = testContext.getAuthToken();
        var response = RestAssured.given()
                .cookie("AUTH_TOKEN", authToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/admin/reservations/" + reservationId + "/status")
                .then()
                .extract();

        testContext.setResponse(response);
    }
}
