package com.camping.admin.steps;

import com.camping.admin.helpers.ApiHelper;
import com.camping.admin.helpers.CampsiteTestHelper;
import com.camping.admin.helpers.ReservationTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.time.LocalDate;

public class ReservationSteps {

    private Long campsiteId;

    @Given("사용 가능한 캠프사이트가 있다")
    public void availableCampsiteExists() {
        String uniqueSiteNumber = "RESERVE-" + System.currentTimeMillis();
        campsiteId = CampsiteTestHelper.createAndGetCampsiteId(uniqueSiteNumber, "예약 테스트용 사이트", 4);
    }

    @When("관리자가 새로운 예약을 생성한다")
    public void createNewReservation() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        Response response = ReservationTestHelper.createReservation("홍길동", startDate, endDate, campsiteId, "010-1234-5678");
        ReservationTestHelper.setResponse(response);
    }

    @Then("예약 생성에 성공한다")
    public void reservationCreatedSuccessfully() {
        ReservationTestHelper.getLastResponse().then().statusCode(201);
    }

    @Given("해당 캠프사이트에 예약이 존재한다")
    public void reservationExistsForCampsite() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        ReservationTestHelper.createReservation("기존고객", startDate, endDate, campsiteId, "010-0000-0000");
    }

    @When("관리자가 중복된 날짜로 예약을 생성한다")
    public void createOverlappingReservation() {
        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(4);
        Response response = ReservationTestHelper.createReservation("신규고객", startDate, endDate, campsiteId, "010-9999-9999");
        ReservationTestHelper.setResponse(response);
    }

    @Then("예약 생성에 실패한다")
    public void reservationCreationFails() {
        Response response = ReservationTestHelper.getLastResponse();
        ApiHelper.assertClientError(response);
    }

    @When("관리자가 존재하지 않는 캠프사이트로 예약을 생성한다")
    public void createReservationWithNonExistentCampsite() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        Response response = ReservationTestHelper.createReservation("홍길동", startDate, endDate, 999999L, "010-1234-5678");
        ReservationTestHelper.setResponse(response);
    }

    @When("관리자가 과거 날짜로 예약을 생성한다")
    public void createReservationWithPastDate() {
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().minusDays(3);
        Response response = ReservationTestHelper.createReservation("홍길동", startDate, endDate, campsiteId, "010-1234-5678");
        ReservationTestHelper.setResponse(response);
    }

    @Given("{string} 상태인 예약이 있다")
    public void stateReservation(String status) {
        Long reservationId = ReservationTestHelper.findOrModifyReservationByStatus(status);
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

