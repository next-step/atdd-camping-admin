package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 예약 관련 API 호출을 담당하는 유틸 클래스
 *
 * [중요] 이 클래스는 Step 클래스가 아닙니다! (@Given 등 없음)
 * 따라서 @Component를 붙여서 Spring 빈으로 등록해야 합니다.
 *
 * TestContext에서 토큰과 예약ID를 가져와서 API를 호출합니다.
 */
@Component
public class ReservationAPI {

    @Autowired
    private TestContext testContext;  // 공유 저장소

    /**
     * 예약 상태 변경 API 호출
     * PATCH /admin/reservations/{id}/status
     */
    public ExtractableResponse<Response> 예약_상태_변경(String newStatus) {
        Map<String, String> requestBody = Map.of("status", newStatus);

        var response = RestAssured
                .given()
                    .header("Authorization", "Bearer " + testContext.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                .when()
                    .patch("/admin/reservations/{id}/status", testContext.getReservation().getId())
                .then()
                    .extract();

        testContext.setResponse(response);
        return response;
    }

    /**
     * 특정 예약 ID로 상태 변경 API 호출
     * PATCH /admin/reservations/{id}/status
     */
    public ExtractableResponse<Response> 예약_상태_변경_by_ID(Long reservationId, String newStatus) {
        Map<String, String> requestBody = Map.of("status", newStatus);

        var response = RestAssured
                .given()
                    .header("Authorization", "Bearer " + testContext.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                .when()
                    .patch("/admin/reservations/{id}/status", reservationId)
                .then()
                    .extract();

        testContext.setResponse(response);
        return response;
    }

    /**
     * 요청 본문 없이 상태 변경 API 호출
     * PATCH /admin/reservations/{id}/status
     */
    public ExtractableResponse<Response> 예약_상태_변경_본문없이() {
        var response = RestAssured
                .given()
                    .header("Authorization", "Bearer " + testContext.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .patch("/admin/reservations/{id}/status", testContext.getReservation().getId())
                .then()
                    .extract();

        testContext.setResponse(response);
        return response;
    }
}
