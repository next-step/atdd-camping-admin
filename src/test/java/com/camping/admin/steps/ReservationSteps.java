package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {
    private int reservationId;

    @Given("관리자가 로그인을 한다.")
    public void 관리자가로그인을한다() {
        Map<String, String> params = Map.of("username", "admin", "password", "admin123");

        ExtractableResponse<Response> response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(params)
                .when()
                .post("http://localhost:8081/auth/login")
                .then()
                .statusCode(200)
                .extract();

        StepContext.setAccessToken(response.jsonPath().get("accessToken"));
    }

    @When("관리자가 예약 상태를 \"CONFIRMED\"로 변경한다.")
    public void 관리자가예약상태를CONFIRMED로변경한다() {
        reservationId = 1;
        String body = "{\"status\": \"CONFIRMED\"}";

        RestAssured.given()
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .patch("http://localhost:8081/admin/reservations/" + reservationId + "/status")
                .then()
                .statusCode(200);
    }

    @Then("예약 상태가 변경된다.")
    public void 예약상태가변경된다() {
        ExtractableResponse<Response> response = RestAssured.given()
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .when()
                .get("http://localhost:8081/admin/reservations")
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