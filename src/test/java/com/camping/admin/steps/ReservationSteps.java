package com.camping.admin.steps;

import com.camping.admin.fixture.TestConfig;
import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.RestAssuredConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

import java.util.Map;

public class ReservationSteps {

    private final SharedState state;

    public ReservationSteps(SharedState state) {
        this.state = state;
    }

    // === Given ===

    @Given("확정된 예약이 존재한다")
    public void 확정된_예약이_존재한다() {
        state.setReservationId(TestConfig.ReservationIds.CONFIRMED);
    }

    @Given("존재하지 않는 예약 ID를 사용한다")
    public void 존재하지_않는_예약_ID를_사용한다() {
        state.setReservationId(TestConfig.ReservationIds.NOT_EXIST);
    }

    // === When ===

    @When("예약 목록을 조회하면")
    public void 예약_목록을_조회하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .when()
                .get("/admin/reservations"));
    }

    @When("해당 예약을 체크인 상태로 변경하면")
    public void 해당_예약을_체크인_상태로_변경하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .body(Map.of("status", "CHECKED_IN"))
                .when()
                .patch("/admin/reservations/" + state.getReservationId() + "/status"));
    }

    @When("해당 예약을 빈 상태로 변경하면")
    public void 해당_예약을_빈_상태로_변경하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .body(Map.of("status", ""))
                .when()
                .patch("/admin/reservations/" + state.getReservationId() + "/status"));
    }
}
