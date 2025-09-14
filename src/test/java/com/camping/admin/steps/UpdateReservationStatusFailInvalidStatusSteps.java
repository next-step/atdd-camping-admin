package com.camping.admin.steps;

import com.camping.admin.CommonContext;
import com.camping.admin.client.ReservationClient;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UpdateReservationStatusFailInvalidStatusSteps {
    private final ReservationClient reservationClient = new ReservationClient();
    String invalidStatus;

    @When("관리자가 잘못된 상태값을 사용해서 상태값을 변경한다")
    public void 관리자가잘못된상태값을사용해서상태값을변경한다() {
        invalidStatus = "INVALID_STATUS";

        CommonContext.lastResponse = reservationClient.updateStatusRaw(
            CommonContext.reservationId,
            invalidStatus
        );
    }

    @Then("예약 상태를 변경하면 실패한다2")
    public void 예약상태를변경하면실패한다2() {
        int status = CommonContext.lastResponse.statusCode();
        assertThat(status).isEqualTo(500);
    }
}
