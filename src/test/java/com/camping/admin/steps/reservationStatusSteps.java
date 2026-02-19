package com.camping.admin.steps;

import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class reservationStatusSteps {

    @Autowired private TestContext context;
    @Autowired private ReservationRepository reservationRepository;

    // ── 상태 사전 설정 ─────────────────────────────────────────

    @Given("예약이 체크인 상태이다")
    public void 예약이체크인상태이다() {
        updateReservationStatus("CHECKED_IN");
    }

    @Given("예약이 체크아웃 상태이다")
    public void 예약이체크아웃상태이다() {
        updateReservationStatus("CHECKED_OUT");
    }

    @Given("예약이 취소 상태이다")
    public void 예약이취소상태이다() {
        updateReservationStatus("CANCELLED");
    }

    @Given("예약이 거절 상태이다")
    public void 예약이거절상태이다() {
        updateReservationStatus("REJECTED");
    }

    private void updateReservationStatus(String status) {
        var reservation = reservationRepository.findById(context.reservationId).orElseThrow();
        reservation.setStatus(status);
        reservationRepository.save(reservation);
    }

    // ── 정상 상태 변경 ─────────────────────────────────────────

    @When("예약 상태를 체크인으로 변경한다")
    public void 예약상태를체크인으로변경한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "CHECKED_IN"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("예약 상태를 체크아웃으로 변경한다")
    public void 예약상태를체크아웃으로변경한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "CHECKED_OUT"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("예약 상태를 확정으로 변경한다")
    public void 예약상태를확정으로변경한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "CONFIRMED"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("예약 상태를 대기로 변경한다")
    public void 예약상태를대기로변경한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "WAITING"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("현재와 동일한 상태로 변경을 시도한다")
    public void 현재와동일한상태로변경을시도한다() {
        // Background에서 CONFIRMED 상태로 생성됨
        context.response = context.authRequest()
                .body(Map.of("status", "CONFIRMED"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    // ── 유효하지 않은 상태 값 ──────────────────────────────────

    @When("존재하지 않는 상태 값으로 변경을 시도한다")
    public void 존재하지않는상태값으로변경을시도한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "HELLO_WORLD"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("소문자로 된 상태 값으로 변경을 시도한다")
    public void 소문자로된상태값으로변경을시도한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "confirmed"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("숫자 값으로 상태 변경을 시도한다")
    public void 숫자값으로상태변경을시도한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "12345"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("특수문자가 포함된 상태 값으로 변경을 시도한다")
    public void 특수문자가포함된상태값으로변경을시도한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "CONFIRMED!@#"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("SQL 인젝션 문자열로 상태 변경을 시도한다")
    public void SQL인젝션문자열로상태변경을시도한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "'; DROP TABLE reservations;--"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    // ── 빈 값 처리 ────────────────────────────────────────────

    @When("빈 문자열로 상태 변경을 시도한다")
    public void 빈문자열로상태변경을시도한다() {
        context.response = context.authRequest()
                .body(Map.of("status", ""))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("공백 문자열로 상태 변경을 시도한다")
    public void 공백문자열로상태변경을시도한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "   "))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("status 필드 없이 상태 변경을 시도한다")
    public void status필드없이상태변경을시도한다() {
        context.response = context.authRequest()
                .body(Map.of("other", "value"))
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    @When("빈 요청 본문으로 상태 변경을 시도한다")
    public void 빈요청본문으로상태변경을시도한다() {
        context.response = context.authRequest()
                .body("{}")
                .patch("/admin/reservations/" + context.reservationId + "/status");
    }

    // ── 존재하지 않는 예약 ─────────────────────────────────────

    @When("존재하지 않는 예약의 상태를 변경한다")
    public void 존재하지않는예약의상태를변경한다() {
        context.response = context.authRequest()
                .body(Map.of("status", "CONFIRMED"))
                .patch("/admin/reservations/99999/status");
    }

    // ── Then / And ────────────────────────────────────────────

    @Then("상태 변경에 성공한다")
    public void 상태변경에성공한다() {
        context.response.then().statusCode(200);
    }

    @Then("상태 변경이 거부된다")
    public void 상태변경이거부된다() {
        context.response.then().statusCode(400);
    }

    @Then("예약을 찾을 수 없다")
    public void 예약을찾을수없다() {
        context.response.then().statusCode(404);
    }

    @And("예약 상태는 체크인이다")
    public void 예약상태는체크인이다() {
        context.response.then().body("status", equalTo("CHECKED_IN"));
    }

    @And("예약 상태는 체크아웃이다")
    public void 예약상태는체크아웃이다() {
        context.response.then().body("status", equalTo("CHECKED_OUT"));
    }

    // 오류 메시지 내용 검증은 AssertJ의 문자열 포함 검사가 더 자연스러움
    @And("유효하지 않은 상태임을 알리는 오류가 반환된다")
    public void 유효하지않은상태임을알리는오류가반환된다() {
        assertThat(context.response.body().asString()).containsIgnoringCase("유효하지 않은 상태");
    }

    @And("허용되지 않는 상태 전이임을 알리는 오류가 반환된다")
    public void 허용되지않는상태전이임을알리는오류가반환된다() {
        assertThat(context.response.body().asString()).containsIgnoringCase("상태 전이");
    }

    @And("동일한 상태로 변경할 수 없음을 알리는 오류가 반환된다")
    public void 동일한상태로변경할수없음을알리는오류가반환된다() {
        assertThat(context.response.body().asString()).containsIgnoringCase("동일한 상태");
    }
}
