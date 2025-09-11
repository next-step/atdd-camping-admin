package com.camping.admin.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class RentalTestFixture {
    public static Map<String, Object> 대여기록목록조회(long rentalId) {
        ExtractableResponse<Response> response = RestAssured.given()
                .spec(StepContext.getRequestSpecification())
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .when()
                .get("/admin/rentals")
                .then()
                .statusCode(200)
                .extract();

        Map<String, Object> rental = (Map<String, Object>) response.jsonPath().getList("$").stream()
                .filter(r -> ((Integer) ((Map<String, Object>) r).get("id")) == rentalId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("대여 기록을 찾을 수 없습니다."));
        return rental;
    }
}
