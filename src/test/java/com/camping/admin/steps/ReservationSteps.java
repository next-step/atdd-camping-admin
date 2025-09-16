package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import static com.camping.admin.hooks.TokenHook.testContext;

public class ReservationSteps {
    @Given("사용자가 예약을 한다.")
    public void reserve() {
        testContext.setReservationId(1L);
        updateReservation( "CONFIRMED");
    }

    @When("관리자가 예약을 {string} 한다.")
    public void updateReservation(String status) {
        String url = "/admin/reservations/" + testContext.getReservationId() + "/status";
        Map<String, Object> request = Map.of("status", status);
        
        var response = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + testContext.getAdminToken())
            .body(request)
            .when().patch(url)
            .then().log().all()
            .extract().response();

        testContext.setResponse(response);
    }

    @Then("예약 상태 변경이 성공한다.")
    public void success() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Then("예약이 {string} 된다.")
    public void successUpdateReservation(String status) {
        var response = testContext.getResponse();
        assertThat(response.jsonPath().getString("status")).isEqualTo(status);
    }

    @Then("존재하지 않는 예약 상태 변경을 실패한다.")
    public void badRequestReservation() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Then("변경할 수 없는 예약 상태 변경을 실패한다.")
    public void conflictReservation() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(409);
    }
}
