package com.camping.admin.client;

import com.camping.admin.steps.TestContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReservationAdminClient {

    @Autowired
    private TestContext testContext;

    @Autowired
    private ObjectMapper objectMapper;

    public void 예약_상태를_변경한다(String jsonBody) {
        try {
            Map<String, Object> requestBody = objectMapper.readValue(jsonBody, new TypeReference<>() {});
            예약_상태를_변경한다(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패: " + jsonBody, e);
        }
    }

    public void 예약_상태를_변경한다(Map<String, Object> requestBody) {
        var reservationId = testContext.getReservationId();
        예약_상태를_변경한다(reservationId, requestBody);
    }

    public void 예약_상태를_변경한다(long reservationId, Map<String, Object> requestBody) {
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
