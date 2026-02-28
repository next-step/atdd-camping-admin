package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class reservationCancellationSteps {

    @Autowired private TestContext context;
    @Autowired private ReservationRepository reservationRepository;

    // ── 액션 ──────────────────────────────────────────────────

    @When("관리자가 예약을 취소한다")
    public void 관리자가예약을취소한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "CANCELLED"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    // ── Then / And ────────────────────────────────────────────

    @Then("예약 취소에 성공한다")
    public void 예약취소에성공한다() {
        context.response.then().statusCode(200);
    }

    @And("예약 상태는 취소됨이다")
    public void 예약상태는취소됨이다() {
        context.response.then().body("status", equalTo("CANCELLED"));
    }

    /**
     * 취소된 예약은 더 이상 해당 날짜의 활성 예약(CONFIRMED, CHECKED_IN)에 포함되지 않아야 한다.
     * findOverlappingReservations 는 CANCELLED 상태를 제외하므로, 결과가 비어 있으면
     * 해당 캠프사이트가 동일 날짜에 재예약 가능한 상태임을 의미한다.
     */
    @And("캠프사이트는 재예약 가능한 상태이다")
    public void 캠프사이트는재예약가능한상태이다() {
        // Background 예약과 동일한 날짜 범위로 겹치는 활성 예약을 조회
        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                context.campsiteId,
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );
        assertThat(overlapping).isEmpty();
    }

    @Then("예약 취소가 거부된다")
    public void 예약취소가거부된다() {
        context.response.then().statusCode(409);
    }
}
