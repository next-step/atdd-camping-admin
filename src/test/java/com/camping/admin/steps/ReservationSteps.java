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
        // 이미 Given에서 설정된 reservationId 사용
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

    @When("관리자가 잘못된 예약 상태로 변경한다.")
    public void 관리자가잘못된예약상태로변경한다() {
        Map<String, String> body = Map.of("status", "INVALID_STATUS");
        ExtractableResponse<Response> response = 예약_상태_변경(reservationId, body);
        statusCode = response.statusCode();
    }

    // 체크인/체크아웃 관련 Steps
    @Given("오늘이 체크인 날짜인 예약이 있다.")
    public void 오늘이체크인날짜인예약이있다() {
        Map<String, Object> reservation = 오늘_체크인_날짜인_예약_조회();
        reservationId = (int) reservation.get("id");
    }

    @When("관리자가 예약을 체크인 처리한다.")
    public void 관리자가예약을체크인처리한다() {
        ExtractableResponse<Response> response = 예약_체크인_처리(reservationId);
        statusCode = response.statusCode();
    }

    @Then("체크인이 성공적으로 처리된다.")
    public void 체크인이성공적으로처리된다() {
        assertThat(statusCode).isEqualTo(200);
    }

    @Then("예약 상태가 {string} 으로 변경된다.")
    public void 예약상태가변경된다2(String expectedStatus) {
        Map<String, Object> reservation = 특정_예약_조회(reservationId);
        assertThat(reservation.get("status")).isEqualTo(expectedStatus);
    }

    @Given("체크인 날짜가 아직 되지 않은 예약이 있다.")
    public void 체크인날짜가아직되지않은예약이있다() {
        Map<String, Object> reservation = 체크인_날짜가_아직_되지_않은_예약_조회();
        reservationId = (int) reservation.get("id");
    }

    @Then("체크인에 실패한다.")
    public void 체크인에실패한다() {
        assertThat(statusCode).isEqualTo(400);
    }

    @Then("다음 오류 메시지가 반환된다: {string}")
    public void 다음오류메시지가반환된다(String expectedMessage) {
        // 실제 에러 메시지 검증은 나중에 구현
        // 현재는 상태 코드만 확인
        assertThat(statusCode).isIn(400, 409);
    }

    @Given("이미 체크인된 예약이 있다.")
    public void 이미체크인된예약이있다() {
        Map<String, Object> reservation = 이미_체크인된_예약_조회();
        reservationId = (int) reservation.get("id");
    }

    @When("관리자가 이미 체크인된 예약을 체크인 처리한다.")
    public void 관리자가이미체크인된예약을체크인처리한다() {
        ExtractableResponse<Response> response = 예약_체크인_처리(reservationId);
        statusCode = response.statusCode();
    }

    @Given("체크인된 예약이 있다.")
    public void 체크인된예약이있다() {
        Map<String, Object> reservation = 이미_체크인된_예약_조회();
        reservationId = (int) reservation.get("id");
    }

    @When("관리자가 예약을 체크아웃 처리한다.")
    public void 관리자가예약을체크아웃처리한다() {
        ExtractableResponse<Response> response = 예약_체크아웃_처리(reservationId);
        statusCode = response.statusCode();
    }

    @Then("체크아웃이 성공적으로 처리된다.")
    public void 체크아웃이성공적으로처리된다() {
        assertThat(statusCode).isEqualTo(200);
    }

    @Given("체크인하지 않은 예약이 있다.")
    public void 체크인하지않은예약이있다() {
        Map<String, Object> reservation = 체크인하지_않은_예약_조회();
        reservationId = (int) reservation.get("id");
    }

    @Then("체크아웃에 실패한다.")
    public void 체크아웃에실패한다() {
        assertThat(statusCode).isEqualTo(400);
    }
}