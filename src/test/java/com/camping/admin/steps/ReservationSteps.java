package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static com.camping.admin.steps.ReservationTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {
    private int reservationId;
    private int statusCode;
    private String reservationStatus;
    private ExtractableResponse<Response> reservationListResponse;

    @When("관리자가 예약 상태를 {string} 로 변경한다.")
    public void 관리자가예약상태를변경한다(String status) {
        reservationStatus = status;
        Map<String, String> body = Map.of("status", reservationStatus);
        ExtractableResponse<Response> response = 예약_상태_변경(reservationId, body);
        statusCode = response.statusCode();
    }

    @Then("예약 상태가 {string} 로 변경된다.")
    public void 예약상태가변경된다(String expectedStatus) {
        Map<String, Object> reservation = 특정_예약_조회(reservationId);
        assertThat(reservation.get("status")).isEqualTo(expectedStatus);
    }

    @Then("예약 상태 변경이 실패한다.")
    public void 예약상태변경이실패한다() {
        assertThat(statusCode).isEqualTo(500);
    }

    @When("관리자가 예약 상태를 동일 상태로 변경한다.")
    public void 관리자가예약상태를동일상태로변경한다() {
        reservationId = 12;
        reservationStatus = "CONFIRMED";
        Map<String, String> body = Map.of("status", reservationStatus);
        ExtractableResponse<Response> response = 예약_상태_변경(reservationId, body);
        statusCode = response.statusCode();
    }

    @Then("예약 상태가 유지된다.")
    public void 예약상태가유지된다() {
        Map<String, Object> reservation = 특정_예약_조회(reservationId);
        assertThat(reservation.get("status")).isEqualTo("CONFIRMED");
    }

    @Given("존재하지 않는 예약이 있다.")
    public void 존재하지않는예약이있다() {
        reservationId = 1000;
    }

    @Given("예약 상태가 {string} 인 예약이 있다.")
    public void 예약이확인된예약이있다(String reservationStatus) {
        Map<String, Object> reservation = 예약상태가_CONFIRMED인_특정예약조회(reservationStatus);
        reservationId = (int) reservation.get("id");
    }

    @Given("예약이 존재한다.")
    public void 예약이존재한다() {
        ExtractableResponse<Response> response = 예약_목록_조회();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList(".")).isNotEmpty();
    }

    @When("관리자가 예약 목록을 조회한다.")
    public void 관리자가예약목록을조회한다() {
        reservationListResponse = 예약_목록_조회();
        statusCode = reservationListResponse.statusCode();
    }

    @Then("예약 목록이 조회된다.")
    public void 예약목록이조회된다() {
        assertThat(statusCode).isEqualTo(200);
        assertThat(reservationListResponse.jsonPath().getList(".")).isNotEmpty();
    }
}