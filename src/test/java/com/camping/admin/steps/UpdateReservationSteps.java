package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.DisplayName;

import static com.camping.admin.domain.enums.ReservationStatus.CONFIRMED;
import static com.camping.admin.helper.CommonContext.lastResponse;
import static com.camping.admin.helper.RequestSender.patch;
import static com.camping.admin.helper.ResponseValidator.isBadRequest;
import static com.camping.admin.helper.ResponseValidator.isOk;
import static com.camping.admin.helper.factory.ReservationRequestFactory.updateRentalRequest;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("예약 상태 업데이트 테스트")
public class UpdateReservationSteps {

    private static final String RESERVATIONS_PATH = "/admin/reservations";
    private Long reservationId;

    @Given("예약이 존재한다")
    public void 예약이_존재한다() {
        reservationId = 1L; // data.sql의 예약 데이터 사용
    }

    @When("관리자가 예약 상태를 {string}로 변경한다")
    public void 관리자가_예약_상태를_변경한다(String status) {
        var request = updateRentalRequest(status);
        lastResponse = patch(RESERVATIONS_PATH + "/" + reservationId + "/status", request);
    }

    @Then("예약 상태 업데이트에 성공했다")
    public void 예약_상태_업데이트에_성공했다() {
        isOk(lastResponse);
    }

    @Then("예약 상태 업데이트가 실패한다")
    public void 예약_상태_업데이트가_실패한다() {
        isBadRequest(lastResponse);
    }

    @And("예약 상태가 {string}로 변경되었다")
    public void 예약_상태가_변경되었다(String expectedStatus) {
        lastResponse.then()
                .body("status", equalTo(expectedStatus));
    }

    @Given("예약이 존재하지 않는다.")
    public void 예약이_존재하지_않는다() {
        reservationId = 999L;
    }

    @When("관리자가 예약 상태를 변경한다")
    public void 관리자가_예약상태를_변경한다() {
        var request = updateRentalRequest(CONFIRMED.name());
        lastResponse = patch(RESERVATIONS_PATH + "/" + reservationId + "/status", request);
    }
}
