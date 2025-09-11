package com.camping.admin.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static com.camping.admin.steps.StepContext.getAccessToken;
import static com.camping.admin.steps.StepContext.getRequestSpecification;

public class RentalTestFixture {
    public static Map<String, Object> 특정대여기록조회(long rentalId) {
        ExtractableResponse<Response> response = 대여기록목록조회();
        Map<String, Object> rental = (Map<String, Object>) response.jsonPath().getList("$").stream()
                .filter(r -> ((Integer) ((Map<String, Object>) r).get("id")) == rentalId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("대여 기록을 찾을 수 없습니다."));
        return rental;
    }

    public static ExtractableResponse<Response> 대여기록목록조회() {
        return RestAssured.given()
                .spec(getRequestSpecification())
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .when()
                .get("/admin/rentals")
                .then()
                .statusCode(200)
                .extract();
    }

    public static ExtractableResponse<Response> 대여기록작성요청(Map<String, Integer> body) {
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
