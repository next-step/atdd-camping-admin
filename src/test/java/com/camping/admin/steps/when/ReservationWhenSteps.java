package com.camping.admin.steps.when;

import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.RestAssuredConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

import java.util.Map;

public class ReservationWhenSteps {

    private final SharedState state;

    public ReservationWhenSteps(SharedState state) {
        this.state = state;
    }

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
