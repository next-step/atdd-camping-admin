package com.camping.admin.steps;

import com.camping.admin.CommonContext;
import com.camping.admin.client.ReservationClient;
import com.camping.admin.dto.ReservationResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateReservationStatusCancelSteps {
    private final ReservationClient reservationClient = new ReservationClient();

    @When("관리자가 예약 상태를 취소했다")
    public void 관리자가예약상태를취소했다() {
        reservationClient.updateStatus(CommonContext.reservationId, "CANCELED");
    }

    @Then("예약 상태는 취소 상태이다")
    public void 예약상태는취소상태이다() {
        ReservationResponse reservation = reservationClient.getReservation(CommonContext.reservationId);
        assertThat(reservation.getStatus()).isEqualTo("CANCELED");
    }

    @And("취소된 예약은 재예약이 가능하다")
    public void 취소된예약은재예약이가능하다() {
        ReservationResponse reservation = reservationClient.getReservation(CommonContext.reservationId);
        assertThat(reservation.isReservable()).isEqualTo(true);
    }
}
