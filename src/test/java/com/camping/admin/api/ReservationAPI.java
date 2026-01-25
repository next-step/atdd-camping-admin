package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReservationAPI {

    /**
     * 예약 상태 변경 API 호출
     * PATCH /admin/reservations/{id}/status
     */
    public ExtractableResponse<Response> 예약_상태_변경(String token, Long reservationId, String newStatus) {
        Map<String, String> requestBody = Map.of("status", newStatus);

        return RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                .when()
                    .patch("/admin/reservations/{id}/status", reservationId)
                .then()
                    .extract();
    }

    /**
     * 요청 본문 없이 상태 변경 API 호출
     * PATCH /admin/reservations/{id}/status
     */
    public ExtractableResponse<Response> 예약_상태_변경_본문없이(String token, Long reservationId) {
        return RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .patch("/admin/reservations/{id}/status", reservationId)
                .then()
                    .extract();
    }
}
