package com.camping.admin.steps;

import com.camping.admin.CommonContext;
import com.camping.admin.client.ReservationClient;
import com.camping.admin.dto.ReservationResponse;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.AssertionsForClassTypes;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateReservationConfirmCodeSteps {
    private final ReservationClient reservationClient = new ReservationClient();

    @When("관리자가 확인코드를 {string} 으로 변경한다")
    public void 관리자가확인코드를으로변경한다(String code) {
        reservationClient.updateConfirmCode(CommonContext.reservationId, code);
    }

    @Then("예약의 확인코드는 {string} 이다")
    public void 예약의확인코드는이다(String expected) {
        ReservationResponse reservation = reservationClient.getReservation(CommonContext.reservationId);
        assertThat(reservation.getConfirmationCode()).isEqualTo(expected);
    }

    @When("관리자가 존재하지 않는 예약ID를 사용해서 확인코드를 {string} 로 변경하려고 한다")
    public void 관리자가존재하지않는예약ID를사용해서확인코드를로변경하려고한다(String code) {
        CommonContext.invalidReservationId = 9999L;
        CommonContext.lastResponse = reservationClient.updateConfirmCodeRaw(CommonContext.invalidReservationId, code);
    }

    @Then("예약 확인코드를 변경하면 실패한다")
    public void 예약확인코드를변경하면실패한다() {
        int status = CommonContext.lastResponse.statusCode();
        AssertionsForClassTypes.assertThat(status).isEqualTo(500);
    }

    @When("관리자가 잘못된 확인코드 {string} 로 변경한다")
    public void 관리자가잘못된확인코드로변경한다(String code) {
        CommonContext.lastResponse = reservationClient.updateConfirmCodeRaw(CommonContext.reservationId, code);
    }

    @Then("예약 확인코드를 변경하면 실패한다2")
    public void 예약확인코드를변경하면실패한다2() {
        int status = CommonContext.lastResponse.statusCode();
        AssertionsForClassTypes.assertThat(status).isEqualTo(500);
    }
}
