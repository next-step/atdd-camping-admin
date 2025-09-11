package com.camping.admin.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class ReservationTestFixture {
    public static ExtractableResponse<Response> 예약_상태_변경(long reservationId, Map<String, String> body) {
        return RestAssured.given()
                .spec(StepContext.getRequestSpecification())
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .body(body)
                .when()
                .patch("/admin/reservations/" + reservationId + "/status")
                .then()
                .extract();
    }

    public static Map<String, Object> 특정_예약_조회(long reservationId) {
        return 예약_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> ((Number) r.get("id")).longValue() == reservationId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다."));
    }

    public static ExtractableResponse<Response> 예약_목록_조회() {
        return RestAssured.given()
                .spec(StepContext.getRequestSpecification())
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract();
    }

    public static Map<String, Object> 예약상태가_CONFIRMED인_특정예약조회(String reservationStatus) {
        return 예약_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(s -> s.get("status").equals(reservationStatus))
                .findFirst()
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다."));
    }
}
