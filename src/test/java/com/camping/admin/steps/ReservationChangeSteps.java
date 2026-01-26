package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.UpdateReservationStatusRequest;
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

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ApiClient apiClient;

    // ===== 공통 헬퍼 메서드 =====

    private void setReservationWithStatus(ReservationStatus status) {
        Reservation reservation = testDataFactory.getReservationWithStatus(status);
        scenarioContext.setReservation(reservation);
    }

    private void requestStatusChange(ReservationStatus status) {
        Long reservationId = scenarioContext.getReservationId();
        UpdateReservationStatusRequest requestBody = new UpdateReservationStatusRequest(status);
        Response response = apiClient.patch("/admin/reservations/" + reservationId + "/status", requestBody);
        scenarioContext.setResponse(response);
    }

    private void assertDbStatus(ReservationStatus expectedStatus) {
        Reservation reservation = scenarioContext.getReservation();
        Reservation updated = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(expectedStatus);
    }

    // ===== Given =====

    @Given("확정된 예약이 있다")
    public void 확정된_예약이_있다() {
        setReservationWithStatus(ReservationStatus.CONFIRMED);
    }

    @Given("취소된 예약이 있다")
    public void 취소된_예약이_있다() {
        setReservationWithStatus(ReservationStatus.CANCELLED);
    }

    @Given("체크인된 예약이 있다")
    public void 체크인된_예약이_있다() {
        setReservationWithStatus(ReservationStatus.CHECKED_IN);
    }

    @Given("체크아웃된 예약이 있다")
    public void 체크아웃된_예약이_있다() {
        setReservationWithStatus(ReservationStatus.CHECKED_OUT);
    }

    // ===== When =====
    @When("관리자가 확정된 예약을 취소한다")
    public void 관리자가_확정된_예약을_취소한다() {
        setReservationWithStatus(ReservationStatus.CONFIRMED);
        requestStatusChange(ReservationStatus.CANCELLED);
    }

    @When("관리자가 예약을 체크인한다")
    public void 관리자가_예약을_체크인한다() {
        requestStatusChange(ReservationStatus.CHECKED_IN);
    }

    @When("관리자가 예약을 체크아웃한다")
    public void 관리자가_예약을_체크아웃한다() {
        requestStatusChange(ReservationStatus.CHECKED_OUT);
    }

    @When("관리자가 존재하지 않는 예약을 취소한다")
    public void 관리자가_존재하지_않는_예약을_취소한다() {
        scenarioContext.setReservationId(99999L);
        requestStatusChange(ReservationStatus.CANCELLED);
    }

    @When("관리자가 예약을 취소한다")
    public void 관리자가_예약을_취소한다() {
        requestStatusChange(ReservationStatus.CANCELLED);
    }

    // ===== Then =====

    @Then("예약은 확정 상태다")
    public void 예약은_확정_상태다() {
        var response = scenarioContext.getResponse();
        if (response.statusCode() == 200) {
            assertThat(response.jsonPath().getString("status")).isEqualTo("CONFIRMED");
        }
        assertDbStatus(ReservationStatus.CONFIRMED);
    }

    @Then("예약은 확정 상태를 유지한다")
    public void 예약은_확정_상태를_유지한다() {
        assertDbStatus(ReservationStatus.CONFIRMED);
    }

    @Then("예약은 취소 상태다")
    public void 예약은_취소_상태다() {
        var response = scenarioContext.getResponse();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo("CANCELLED");
        assertDbStatus(ReservationStatus.CANCELLED);
    }

    @Then("예약은 취소 상태를 유지한다")
    public void 예약은_취소_상태를_유지한다() {
        assertDbStatus(ReservationStatus.CANCELLED);
    }

    @Then("예약은 체크인 상태다")
    public void 예약은_체크인_상태다() {
        var response = scenarioContext.getResponse();
        if (response.statusCode() == 200) {
            assertThat(response.jsonPath().getString("status")).isEqualTo("CHECKED_IN");
        }
        assertDbStatus(ReservationStatus.CHECKED_IN);
    }

    @Then("예약은 체크아웃 상태다")
    public void 예약은_체크아웃_상태다() {
        var response = scenarioContext.getResponse();
        if (response.statusCode() == 200) {
            assertThat(response.jsonPath().getString("status")).isEqualTo("CHECKED_OUT");
        }
        assertDbStatus(ReservationStatus.CHECKED_OUT);
    }

    @Then("예약은 체크아웃 상태를 유지한다")
    public void 예약은_체크아웃_상태를_유지한다() {
        assertDbStatus(ReservationStatus.CHECKED_OUT);
    }

    @Then("예약을 찾을 수 없다는 오류가 발생한다")
    public void 예약을_찾을_수_없다는_오류가_발생한다() {
        var response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isIn(400, 404, 500);
    }

    @Then("상태 변경이 거부된다")
    public void 상태_변경이_거부된다() {
        var response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isIn(400, 500);
    }
}