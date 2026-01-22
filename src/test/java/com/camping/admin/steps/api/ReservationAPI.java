package com.camping.admin.steps.api;

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
     *
     * @param newStatus 변경할 상태값 (예: "CANCELLED")
     * @return API 응답
     */
    public ExtractableResponse<Response> 예약_상태_변경(String newStatus) {
        Map<String, String> requestBody = Map.of("status", newStatus);

        return RestAssured
                .given()
                    .header("Authorization", "Bearer " + testContext.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                .when()
                    .patch("/admin/reservations/{id}/status", testContext.getReservationId())
                .then()
                    .extract();
    }
}
