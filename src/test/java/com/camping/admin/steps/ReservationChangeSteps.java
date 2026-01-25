package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.support.ApiClient;
import com.camping.admin.support.ScenarioContext;
import com.camping.admin.support.TestDataFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationChangeSteps extends CucumberSpringConfiguration {

    private static final String INVALID_STATUS = "INVALID_STATUS";

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ApiClient apiClient;

    // ===== 공통 헬퍼 메서드 =====

    private void setReservationWithStatus(String status) {
        Reservation reservation = testDataFactory.getReservationWithStatus(status);
        scenarioContext.setReservation(reservation);
    }

    private void requestStatusChange(String body) {
        Long reservationId = scenarioContext.getReservationId();
        Response response = apiClient.patch("/admin/reservations/" + reservationId + "/status", body);
        scenarioContext.setResponse(response);
    }

    private void assertDbStatus(String expectedStatus) {
        Reservation reservation = scenarioContext.getReservation();
        Reservation updated = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(expectedStatus);
    }

    // ===== Given =====

    @Given("확정된 예약이 있다")
    public void 확정된_예약이_있다() {
        setReservationWithStatus("CONFIRMED");
    }

    @Given("취소된 예약이 있다")
    public void 취소된_예약이_있다() {
        setReservationWithStatus("CANCELLED");
    }

    // ===== When =====
    @When("관리자가 확정된 예약을 취소한다")
    public void 관리자가_확정된_예약을_취소한다() {
        setReservationWithStatus("CONFIRMED");
        requestStatusChange("{\"status\":\"CANCELLED\"}");
    }

    @When("관리자가 존재하지 않는 예약을 취소한다")
    public void 관리자가_존재하지_않는_예약을_취소한다() {
        scenarioContext.setReservationId(99999L);
        requestStatusChange("{\"status\":\"CANCELLED\"}");
    }

    @When("관리자가 예약을 취소한다")
    public void 관리자가_예약을_취소한다() {
        requestStatusChange("{\"status\":\"CANCELLED\"}");
    }

    @When("관리자가 빈 요청을 보낸다")
    public void 관리자가_빈_요청을_보낸다() {
        requestStatusChange("{}");
    }

    @When("관리자가 상태값 없이 요청한다")
    public void 관리자가_상태값_없이_요청한다() {
        requestStatusChange("{\"other\":\"value\"}");
    }

    @When("관리자가 유효하지 않은 상태값으로 변경을 요청한다")
    public void 관리자가_유효하지_않은_상태값으로_변경을_요청한다() {
        scenarioContext.setRequestedStatus(INVALID_STATUS);
        requestStatusChange("{\"status\":\"" + INVALID_STATUS + "\"}");
    }

    @When("관리자가 빈 문자열로 상태 변경을 요청한다")
    public void 관리자가_빈_문자열로_상태_변경을_요청한다() {
        requestStatusChange("{\"status\":\"\"}");
    }

    // ===== Then =====

    @Then("예약은 취소 상태다")
    public void 예약은_취소_상태다() {
        var response = scenarioContext.getResponse();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo("CANCELLED");
        assertDbStatus("CANCELLED");
    }

    @Then("예약을 찾을 수 없다는 오류가 발생한다")
    public void 예약을_찾을_수_없다는_오류가_발생한다() {
        var response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isIn(400, 404, 500);
    }

    @Then("잘못된 요청이라는 응답을 받는다")
    public void 잘못된_요청이라는_응답을_받는다() {
        var response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Then("예약은 기존 상태를 유지한다")
    public void 예약은_기존_상태를_유지한다() {
        String originalStatus = scenarioContext.getOriginalStatus();
        assertDbStatus(originalStatus);
    }

    @Then("예약 상태가 해당 값으로 변경된다")
    public void 예약_상태가_해당_값으로_변경된다() {
        var response = scenarioContext.getResponse();
        String requestedStatus = scenarioContext.getRequestedStatus();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo(requestedStatus);
        assertDbStatus(requestedStatus);
    }
}