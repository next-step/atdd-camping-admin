package com.camping.admin.steps;

import com.camping.admin.helpers.ApiHelper;
import com.camping.admin.helpers.ReservationTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class ReservationSteps {

    @Given("{string} 상태인 예약이 있다")
    public void stateReservation(String status) {
        Long reservationId = ReservationTestHelper.findOrCreateReservationByStatus(status);
        ReservationTestHelper.setReservationId(reservationId);
    }

    @When("예약을 {string} 상태로 변경한다")
    public void changeReservation(String newStatus) {
        Long reservationId = ReservationTestHelper.getReservationId();
        Response response = ReservationTestHelper.patchReservationStatus(reservationId, newStatus);
        ReservationTestHelper.setResponse(response);
    }

    @Then("예약 상태가 {string}으로 변경된다")
    public void completeReservation(String expectedStatus) {
        Response lastResponse = ReservationTestHelper.getLastResponse();
        Long id = ReservationTestHelper.getReservationId();

        ApiHelper.assertReservationStatusAndId(lastResponse, expectedStatus, id);
    }

    @Given("존재하지 않는 예약 번호를 사용한다")
    public void useNonExistingReservationId() {
        ReservationTestHelper.setReservationId(999999L);
    }

    @When("예약 상태 변경 정보를 제공하지 않고 요청한다")
    public void requestStatusChangeWithoutInfo() {
        Long reservationId = ReservationTestHelper.getReservationId();
        Response response = ReservationTestHelper.patchReservationStatusWithoutBody(reservationId);
        ReservationTestHelper.setResponse(response);
    }

    @Then("상태 변경이 실패한다")
    public void statusChangeFails() {
        Response lastResponse = ReservationTestHelper.getLastResponse();
        ApiHelper.assertClientError(lastResponse);
    }

}

