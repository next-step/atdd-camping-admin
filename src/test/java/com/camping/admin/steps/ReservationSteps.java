package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.fixture.ReservationFixture;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Map;

import static com.camping.admin.api.ReservationApiClient.sendChangeStatus;
import static com.camping.admin.context.AuthContext.getAccessToken;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps extends BaseSteps {
    long reservationId;
    ExtractableResponse<Response> response;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private CampsiteRepository campsiteRepository;

    @Sql
    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        var reservation = ReservationFixture.builder()
                .campsite(campsiteRepository.findById(1L).get())
                .build();
        var reservationEntity = reservationRepository.save(reservation);
        this.reservationId = reservationEntity.getId();
    }

    @When("관리자가 예약을 {string} 상태로 변경한다")
    public void 관리자가예약상태를변경한다(String status) {
        response = sendChangeStatus(getAccessToken(), reservationId, Map.of("status", status));
    }

    @Then("예약은 취소된다")
    public void 예약은취소된다() {
        String status = response.jsonPath().getString("status");
        long reservationId = response.jsonPath().getInt("id");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(reservationId).isEqualTo(this.reservationId);
        assertThat(status).isEqualTo("CANCELLED");
    }

    @Given("예약이 존재하지 않는다")
    public void 예약이존재하지않는다() {
        reservationId = 99999;
    }

    @Then("변경은 실패한다")
    public void 변경은실패한다() {
        assertThat(response.statusCode()).isEqualTo(500);
    }

    @When("관리자가 상태값을 입력하지 않고 변경한다")
    public void 관리자가상태값을입력하지않고변경한다() {
        response = sendChangeStatus(getAccessToken(), reservationId, Map.of("hasNotStatus", "DUMMY"));
    }

    @Then("예약 상태는 변경되지 않는다")
    public void 예약상태는변경되지않는다() {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo("CONFIRMED");
    }
}
