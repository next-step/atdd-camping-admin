package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.Map;

import static com.camping.admin.support.Role.관리자;
import static org.assertj.core.api.Assertions.assertThat;

import static com.camping.admin.hooks.TokenHook.context;

public class ReservationSteps {
    @Given("사용자가 예약을 한다.")
    public void reserve() {
        context.setData("reservationId", 1L);
        updateReservation( "CONFIRMED");
    }

    @When("관리자가 예약을 {string} 한다.")
    public void updateReservation(String status) {
        String url = "/admin/reservations/" + context.getData("reservationId") + "/status";
        Map<String, Object> request = Map.of("status", status);
        
        var response = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + context.getToken(관리자))
            .body(request)
            .when().patch(url)
            .then().log().all()
            .extract().response();

        context.setResponse(response);
    }

    @Then("예약 상태 변경이 성공 된다.")
    public void success() {
        var response = context.getResponse();
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Then("예약이 {string} 된다.")
    public void successUpdateReservation(String status) {
        var response = context.getResponse();
        assertThat(response.jsonPath().getString("status")).isEqualTo(status);
    }

    @Then("존재하지 않는 예약 상태 변경이 실패 된다.")
    public void badRequestReservation() {
        var response = context.getResponse();
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Then("{string} 메시지가 응답 된다.")
    public void checkErrorMessage(String expectedMessage) {
        var response = context.getResponse();
        assertThat(response.jsonPath().getString("message")).isEqualTo(expectedMessage);
    }

    @Then("변경할 수 없는 예약 상태 변경이 실패 된다.")
    public void conflictReservation() {
        var response = context.getResponse();
        assertThat(response.statusCode()).isEqualTo(409);
    }

    @When("관리자가 예약 목록을 조회 한다.")
    public void getReservations() {
        var response = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + context.getToken(관리자))
            .when().get("/admin/reservations")
            .then().log().all()
            .extract().response();

        context.setResponse(response);
    }

    @Then("예약 목록 조회가 성공 된다.")
    public void successGetReservations() {
        var response = context.getResponse();
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Then("예약 목록이 {int}개 조회 된다.")
    public void checkReservationCount(int expectedCount) {
        var response = context.getResponse();
        int actualCount = response.jsonPath().getList("$").size();
        assertThat(actualCount).isEqualTo(expectedCount);
    }

    @Given("예약 데이터를 모두 삭제 한다.")
    public void deleteAllReservations() {
        var response = RestAssured.given()
                .contentType(ContentType.JSON)
                .when().delete("/test-api/data/cleanup")
                .then().log().all()
                .extract().response();
        context.setResponse(response);
    }

    @When("관리자가 {string} 상태인 예약 목록을 조회 한다.")
    public void getReservationsByStatus(String status) {
        var response = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + context.getToken(관리자))
            .queryParam("status", status)
            .when().get("/admin/reservations")
            .then().log().all()
            .extract().response();

        context.setResponse(response);
    }

    @Then("예약 상태가 {string} 인 예약만 조회 된다.")
    public void checkReservationsByStatus(String expectedStatus) {
        var response = context.getResponse();
        var reservations = response.jsonPath().getList("$");

        for (int i = 0; i < reservations.size(); i++) {
            String actualStatus = response.jsonPath().getString("[" + i + "].status");
            assertThat(actualStatus).isEqualTo(expectedStatus);
        }
    }
}
