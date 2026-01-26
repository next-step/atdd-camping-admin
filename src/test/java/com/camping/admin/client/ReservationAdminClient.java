package com.camping.admin.client;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReservationAdminClient {

    public ExtractableResponse<Response> 예약_목록을_조회한다(String authToken) {
        return RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .when()
            .get("/admin/reservations")
            .then()
            .extract();
    }

    public ExtractableResponse<Response> 예약_상태를_변경한다(String authToken, Long reservationId, Map<String, Object> body) {
        return RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .patch("/admin/reservations/" + reservationId + "/status")
            .then()
            .extract();
    }
    
    public ExtractableResponse<Response> 잘못된_본문으로_예약_상태를_변경한다(String authToken, Long reservationId, String invalidBody) {
        return RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .contentType(ContentType.JSON)
            .body(invalidBody)
            .when()
            .patch("/admin/reservations/" + reservationId + "/status")
            .then()
            .extract();
    }
}
