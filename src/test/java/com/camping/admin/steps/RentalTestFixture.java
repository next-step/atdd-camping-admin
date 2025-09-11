package com.camping.admin.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static com.camping.admin.steps.StepContext.getAccessToken;
import static com.camping.admin.steps.StepContext.getRequestSpecification;

public class RentalTestFixture {
    public static Map<String, Object> 특정_대여_기록_조회(long rentalId) {
        return 대여_기록_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> ((Integer) r.get("id")) == rentalId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("대여 기록을 찾을 수 없습니다."));
    }

    public static ExtractableResponse<Response> 대여_기록_목록_조회() {
        return RestAssured.given()
                .spec(getRequestSpecification())
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .when()
                .get("/admin/rentals")
                .then()
                .statusCode(200)
                .extract();
    }

    public static ExtractableResponse<Response> 대여_기록_작성_요청(Map<String, Integer> body) {
        return RestAssured.given()
                .spec(getRequestSpecification())
                .header("Authorization", "Bearer " + getAccessToken())
                .body(body)
                .when()
                .post("/admin/rentals")
                .then()
                .extract();
    }
}
