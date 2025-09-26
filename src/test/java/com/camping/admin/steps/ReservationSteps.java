package com.camping.admin.steps;

import com.camping.admin.context.CampsiteContext;
import com.camping.admin.context.ReservationContext;
import com.camping.admin.context.SharedContext;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.fixture.ReservationFixture;
import com.camping.admin.repository.ReservationRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.camping.admin.api.ReservationApiClient.sendChangeStatus;
import static com.camping.admin.context.SharedContext.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps extends BaseSteps {
    private final ReservationContext reservationContext = SharedContext.getReservationContext();
    private final CampsiteContext campsiteContext = SharedContext.getCampsiteContext();

    @Autowired
    private ReservationRepository reservationRepository;

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        var reservation = ReservationFixture.builder().campsite(campsiteContext.getCampsite()).build();
        var reservationEntity = reservationRepository.save(reservation);
        reservationContext.setReservationId(reservationEntity.getId());
    }

    @When("관리자가 예약을 {string} 상태로 변경한다")
    public void 관리자가예약상태를변경한다(String status) {
        var response = sendChangeStatus(getAccessToken(), reservationContext.getReservationId(), Map.of("status", status));
        setResponse(response);
    }

    @Then("예약은 취소된다")
    public void 예약은취소된다() {
        var statusChangeResponse = getResponse();
        String status = statusChangeResponse.jsonPath().getString("status");
        long reservationId = statusChangeResponse.jsonPath().getInt("id");

        assertThat(statusChangeResponse.statusCode()).isEqualTo(200);
        assertThat(reservationId).isEqualTo(reservationContext.getReservationId());
        assertThat(status).isEqualTo(ReservationStatus.CANCELLED.toString());
    }

    @Given("예약이 존재하지 않는다")
    public void 예약이존재하지않는다() {
        reservationContext.setReservationId(99999L);
    }

    @Then("변경은 실패한다")
    public void 변경은실패한다() {
        assertThat(getResponse().statusCode()).isEqualTo(500);
    }

    @When("관리자가 상태값을 입력하지 않고 변경한다")
    public void 관리자가상태값을입력하지않고변경한다() {
        var response = sendChangeStatus(getAccessToken(), reservationContext.getReservationId(), Map.of("hasNotStatus", "DUMMY"));
        setResponse(response);
    }

    @Then("예약 상태는 변경되지 않는다")
    public void 예약상태는변경되지않는다() {
        assertThat(getResponse().statusCode()).isEqualTo(200);
        assertThat(getResponse().jsonPath().getString("status")).isEqualTo("CONFIRMED");
    }

    @And("다른 사용자가 동일 기간으로 예약을 했다")
    public void 다른사용자가동일기간으로예약을했다() {
        var reservation = ReservationFixture.builder()
                .customerName("다른사용자")
                .campsite(campsiteContext.getCampsite())
                .build();
        reservationRepository.save(reservation);
    }
}
