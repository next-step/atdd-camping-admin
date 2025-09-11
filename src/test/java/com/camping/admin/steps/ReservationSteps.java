package com.camping.admin.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {
    private int reservationId;

    @When("관리자가 예약 상태를 \"CONFIRMED\"로 변경한다.")
    public void 관리자가예약상태를CONFIRMED로변경한다() {
        reservationId = 1;
        String body = "{\"status\": \"CONFIRMED\"}";

        RestAssured.given()
                .spec(StepContext.getRequestSpecification())
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .body(body)
                .when()
                .patch("/admin/reservations/" + reservationId + "/status")
                .then()
                .statusCode(200);
    }

    @Then("예약 상태가 \"CONFIRMED\"로 변경된다.")
    public void 예약상태가CONFIRMED로변경된다() {
        ExtractableResponse<Response> response = RestAssured.given()
                .spec(StepContext.getRequestSpecification())
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract();

        Map<String, Object> reservation = (Map<String, Object>) response.jsonPath().getList("$").stream()
                .filter(s -> ((Integer) ((Map<String, Object>) s).get("id")) == reservationId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다."));

        assertThat(reservation.get("status")).isEqualTo("CONFIRMED");
    }
}