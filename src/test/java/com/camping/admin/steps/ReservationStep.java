package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationStep {
    private String adminToken;
    private Long reservationId;
    private Response lastResponse;

    @Given("관리자가 로그인 한다.")
    public void loginAdmin() {
        adminToken = RestAssured.given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when().post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");
    }

    @Given("사용자가 예약을 한다.")
    public void reserve() {
        reservationId = 1L;
    }

    @When("관리자가 예약을 취소 한다.")
    public void cancelReservation() {
        Map<String, Object> statusUpdate = Map.of("status", "CANCELLED");
        
        lastResponse = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + adminToken)
            .body(statusUpdate)
            .when().patch("/admin/reservations/" + reservationId + "/status")
            .then().log().all()
            .extract().response();
    }

    @Then("예약이 취소 된다.")
    public void canceledReservation() {
        assertThat(lastResponse.getStatusCode()).isEqualTo(200);
        assertThat(lastResponse.jsonPath().getString("status")).isEqualTo("CANCELLED");
    }
}
