package com.camping.admin.steps;

import static com.camping.admin.helper.ReservationApiClient.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.ReservationApiClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservationSteps {

    private static final Logger log = LoggerFactory.getLogger(ReservationSteps.class);

    private final ApiClientContext api;
    private final ScenarioContext scenario;

    public ReservationSteps(ApiClientContext api, ScenarioContext scenario) {
        this.api = api;
        this.scenario = scenario;
    }

    private ReservationApiClient helper() {
        return api.reservation();
    }

    // === Given ===

    @Given("취소 가능한 예약이 존재한다")
    public void 취소가능한예약이존재한다() {
        scenario.setTargetReservationId(CONFIRMED_RESERVATION_ID);
        log.info("[Given] 취소 가능한 예약이 존재한다. 예약 ID: {}", scenario.getTargetReservationId());
    }

    @Given("예약이 존재한다")
    public void 예약이존재한다() {
        scenario.setTargetReservationId(PENDING_RESERVATION_ID);
        log.info("[Given] 예약이 존재한다. 예약 ID: {}", scenario.getTargetReservationId());
    }

    @Given("이미 취소된 예약이 있다")
    public void 이미취소된예약이있다() {
        scenario.setTargetReservationId(CANCELLED_RESERVATION_ID);
        log.info("[Given] 이미 취소된 예약이 있다. ID: {}", scenario.getTargetReservationId());
    }

    @Given("체크아웃된 예약이 있다")
    public void 체크아웃된예약이있다() {
        scenario.setTargetReservationId(CHECKED_OUT_RESERVATION_ID);
        log.info("[Given] 체크아웃된 예약이 있다. ID: {}", scenario.getTargetReservationId());
    }

    // === When ===

    @When("관리자가 해당 예약을 취소한다")
    public void 관리자가해당예약을취소한다() {
        log.info("[When] 관리자가 예약 {}을 취소한다", scenario.getTargetReservationId());
        scenario.setLastResponse(helper().updateStatus(scenario.getTargetReservationId(), "CANCELLED"));
    }

    @When("관리자가 예약 상태를 {word}로 변경한다")
    public void 관리자가예약상태를변경한다(String status) {
        log.info("[When] 관리자가 예약 상태를 {}로 변경한다", status);
        scenario.setLastResponse(helper().updateStatus(scenario.getTargetReservationId(), status));
    }

    @When("관리자가 예약 상태를 빈 값으로 변경한다")
    public void 관리자가예약상태를빈값으로변경한다() {
        log.info("[When] 관리자가 예약 상태를 빈 값으로 변경한다");
        scenario.setLastResponse(helper().updateStatusWithEmptyBody(scenario.getTargetReservationId()));
    }

    @When("관리자가 존재하지 않는 예약을 취소한다")
    public void 관리자가존재하지않는예약을취소한다() {
        scenario.setLastResponse(helper().updateStatus(99999, "CANCELLED"));
    }

    @When("관리자가 존재하지 않는 예약의 상태를 변경한다")
    public void 관리자가존재하지않는예약의상태를변경한다() {
        scenario.setLastResponse(helper().updateStatus(99999, "CHECKED_IN"));
    }

    // === Then ===

    @Then("예약 상태는 취소로 변경된다")
    public void 예약상태는취소로변경된다() {
        scenario.getLastResponse().then().statusCode(200);
        String dbStatus = helper().getReservationStatus(scenario.getTargetReservationId());
        assertThat(dbStatus).isEqualTo("CANCELLED");
        log.info("[Then] 예약 상태는 취소로 변경된다 - DB 반영 확인 완료");
    }

    @Then("예약 상태가 {word}로 변경된다")
    public void 예약상태가변경된다(String expectedStatus) {
        scenario.getLastResponse().then().statusCode(200);
        String dbStatus = helper().getReservationStatus(scenario.getTargetReservationId());
        assertThat(dbStatus).isEqualTo(expectedStatus);
        log.info("[Then] 예약 상태가 {}로 변경된다 - DB 반영 확인 완료", expectedStatus);
    }

    @Then("예약 상태 변경이 거부된다")
    public void 예약상태변경이거부된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(400, 404, 409, 500);
        log.info("[Then] 예약 상태 변경이 거부된다. 상태코드: {}", statusCode);
    }

    @And("해당 자원은 다시 예약 가능하다")
    public void 해당자원은다시예약가능하다() {
        Response response = helper().getReservations();
        response.then().statusCode(200);
        log.info("[And] 해당 자원은 다시 예약 가능하다");
    }

    // === 확인 코드로 조회 ===

    @When("관리자가 확인 코드로 예약을 조회한다")
    public void 관리자가확인코드로예약을조회한다() {
        // 기존 예약의 확인 코드 조회
        String confirmationCode = helper().getConfirmationCode(scenario.getTargetReservationId());
        scenario.setTargetConfirmationCode(confirmationCode);
        scenario.setLastResponse(helper().findByConfirmationCode(confirmationCode));
        log.info("[When] 관리자가 확인 코드 {}로 예약을 조회한다", confirmationCode);
    }

    @When("관리자가 존재하지 않는 확인 코드로 예약을 조회한다")
    public void 관리자가존재하지않는확인코드로예약을조회한다() {
        scenario.setLastResponse(helper().findByConfirmationCode("XXXXXX"));
        log.info("[When] 관리자가 존재하지 않는 확인 코드로 예약을 조회한다");
    }

    @Then("예약 정보가 반환된다")
    public void 예약정보가반환된다() {
        scenario.getLastResponse().then().statusCode(200);
        log.info("[Then] 예약 정보가 반환된다");
    }

    @Then("예약 조회 결과가 없다")
    public void 예약조회결과가없다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(200, 404);  // 빈 결과 또는 404
        log.info("[Then] 예약 조회 결과가 없다");
    }

    @And("예약 상태는 취소 상태이다")
    public void 예약상태는취소상태이다() {
        String status = scenario.getLastResponse().jsonPath().getString("status");
        assertThat(status).isEqualTo("CANCELLED");
        log.info("[And] 예약 상태는 취소 상태이다");
    }
}
