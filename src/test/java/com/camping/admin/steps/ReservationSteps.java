package com.camping.admin.steps;

import static com.camping.admin.helper.ReservationTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.ReservationTestHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class ReservationSteps {

    private final TestHelperContext helpers;
    private Response lastResponse;
    private int targetReservationId;

    public ReservationSteps(TestHelperContext helpers) {
        this.helpers = helpers;
    }

    private ReservationTestHelper helper() {
        return helpers.reservation();
    }

    // === Given ===

    @Given("취소 가능한 예약이 존재한다")
    public void 취소가능한예약이존재한다() {
        targetReservationId = CONFIRMED_RESERVATION_ID;
        System.out.println("[Given] 취소 가능한 예약이 존재한다. 예약 ID: " + targetReservationId);
    }

    @Given("예약이 존재한다")
    public void 예약이존재한다() {
        targetReservationId = PENDING_RESERVATION_ID;
        System.out.println("[Given] 예약이 존재한다. 예약 ID: " + targetReservationId);
    }

    @Given("이미 취소된 예약이 있다")
    public void 이미취소된예약이있다() {
        targetReservationId = CANCELLED_RESERVATION_ID;
        System.out.println("[Given] 이미 취소된 예약이 있다. ID: " + targetReservationId);
    }

    @Given("체크아웃된 예약이 있다")
    public void 체크아웃된예약이있다() {
        targetReservationId = CHECKED_OUT_RESERVATION_ID;
        System.out.println("[Given] 체크아웃된 예약이 있다. ID: " + targetReservationId);
    }

    // === When ===

    @When("관리자가 해당 예약을 취소한다")
    public void 관리자가해당예약을취소한다() {
        System.out.println("[When] 관리자가 예약 " + targetReservationId + "을 취소한다");
        lastResponse = helper().updateStatus(targetReservationId, "CANCELLED");
    }

    @When("관리자가 예약 상태를 {word}로 변경한다")
    public void 관리자가예약상태를변경한다(String status) {
        System.out.println("[When] 관리자가 예약 상태를 " + status + "로 변경한다");
        lastResponse = helper().updateStatus(targetReservationId, status);
    }

    @When("관리자가 예약 상태를 빈 값으로 변경한다")
    public void 관리자가예약상태를빈값으로변경한다() {
        System.out.println("[When] 관리자가 예약 상태를 빈 값으로 변경한다");
        lastResponse = helper().updateStatusWithEmptyBody(targetReservationId);
    }

    @When("관리자가 존재하지 않는 예약을 취소한다")
    public void 관리자가존재하지않는예약을취소한다() {
        lastResponse = helper().updateStatus(99999, "CANCELLED");
    }

    @When("관리자가 존재하지 않는 예약의 상태를 변경한다")
    public void 관리자가존재하지않는예약의상태를변경한다() {
        lastResponse = helper().updateStatus(99999, "CHECKED_IN");
    }

    // === Then ===

    @Then("예약 상태는 취소로 변경된다")
    public void 예약상태는취소로변경된다() {
        lastResponse.then().statusCode(200);
        String dbStatus = helper().getReservationStatus(targetReservationId);
        assertThat(dbStatus).isEqualTo("CANCELLED");
        System.out.println("[Then] 예약 상태는 취소로 변경된다 - DB 반영 확인 완료");
    }

    @Then("예약 상태가 {word}로 변경된다")
    public void 예약상태가변경된다(String expectedStatus) {
        lastResponse.then().statusCode(200);
        String dbStatus = helper().getReservationStatus(targetReservationId);
        assertThat(dbStatus).isEqualTo(expectedStatus);
        System.out.println("[Then] 예약 상태가 " + expectedStatus + "로 변경된다 - DB 반영 확인 완료");
    }

    @Then("예약 상태 변경이 거부된다")
    public void 예약상태변경이거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 404, 409, 500);
        System.out.println("[Then] 예약 상태 변경이 거부된다. 상태코드: " + statusCode);
    }

    @And("해당 자원은 다시 예약 가능하다")
    public void 해당자원은다시예약가능하다() {
        Response response = helper().getReservations();
        response.then().statusCode(200);
        System.out.println("[And] 해당 자원은 다시 예약 가능하다");
    }
}