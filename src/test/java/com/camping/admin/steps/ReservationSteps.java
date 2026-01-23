package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.steps.api.ReservationApi;
import com.camping.admin.steps.context.TestContext;
import com.camping.admin.steps.support.TestDataFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
public class ReservationSteps {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ReservationApi reservationApi;

    @Autowired
    private ReservationRepository reservationRepository;

    @Given("{string} 상태의 예약이 등록되어 있다")
    public void reservationWithStatusIsRegistered(String status) {
        Reservation reservation = testDataFactory.createReservation("Test Customer", status);
        testContext.setLastReservationId(reservation.getId());
    }

    @When("관리자가 해당 예약의 상태를 {string}로 변경하면")
    public void adminUpdatesReservationStatus(String status) {
        testContext.setLastResponse(reservationApi.예약_상태_변경_요청(testContext.getAdminToken(), testContext.getLastReservationId(), status));
    }

    @When("관리자가 존재하지 않는 예약 ID의 상태를 {string}로 변경하면")
    public void adminUpdatesNonExistentReservationStatus(String status) {
        testContext.setLastResponse(reservationApi.예약_상태_변경_요청(testContext.getAdminToken(), 9999L, status));
    }

    @Then("예약 상태가 {string}로 변경된다")
    public void reservationStatusShouldBe(String expectedStatus) {
        Reservation reservation = reservationRepository.findById(testContext.getLastReservationId()).orElseThrow();
        assertThat(reservation.getStatus()).isEqualTo(expectedStatus);
    }

    @Then("예약 상태 변경 요청이 실패한다")
    public void reservationStatusUpdateShouldFail() {
        assertThat(testContext.getLastResponse().statusCode()).isGreaterThanOrEqualTo(400);
    }
}