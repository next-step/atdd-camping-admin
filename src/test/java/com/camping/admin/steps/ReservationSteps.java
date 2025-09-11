package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {
    private final TestContext testContext;

    public ReservationSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("사용자가 예약을 한다.")
    public void reserve() {
        testContext.setReservationId(1L);
    }

    @When("관리자가 예약을 {string} 한다.")
    public void cancelReservation(String status) {
        Map<String, Object> request = Map.of("status", status);
        
        var response = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + testContext.getAdminToken())
            .body(request)
            .when().patch("/admin/reservations/" + testContext.getReservationId() + "/status")
            .then().log().all()
            .extract().response();

        testContext.setResponse(response);
    }

    @Then("예약이 {string} 된다.")
    public void canceledReservation(String status) {
        var response = testContext.getResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo(status);
    }

    @Then("요청이 {int} 코드로 실패 한다.")
    public void failWithStatusCode(int statusCode) {
        var response = testContext.getResponse();
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @Then("{string} 메시지가 응답 한다.")
    public void responseMessage(String message) {
        var response = testContext.getResponse();
        assertThat(response.jsonPath().getString("message")).isEqualTo(message);
    }
}
