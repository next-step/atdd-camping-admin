package com.camping.admin.steps;

import com.camping.admin.CommonContext;
import com.camping.admin.client.ReservationClient;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateReservationStatusConfirmedSteps {
    private final ReservationClient reservationClient = new ReservationClient();

    @When("관리자가 예약 상태를 완료했다")
    public void 관리자가예약상태를완료했다() {
        reservationClient.updateStatus(CommonContext.reservationId, ReservationStatus.CONFIRMED);
    }

    @Then("예약 상태는 완료 상태이다")
    public void 예약상태는완료상태이다() {
        ReservationResponse reservation = reservationClient.getReservation(CommonContext.reservationId);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED.name());
    }

    @And("완료된 예약은 재예약이 불가능하다")
    public void 완료된예약은재예약이불가능하다() {
        ReservationResponse reservation = reservationClient.getReservation(CommonContext.reservationId);
        assertThat(reservation.isReservable()).isFalse();
    }
}
