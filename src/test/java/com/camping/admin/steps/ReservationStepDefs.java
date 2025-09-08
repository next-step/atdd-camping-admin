package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ReservationStepDefs {
    private Long reservationId;
    private Response lastResponse;

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        reservationId = 1L;
    }

    @When("관리자가 예약 {int}을 취소했다")
    public void 관리자가예약을취소했다(int reservationId) {
        lastResponse = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(Map.of("status", "CANCELLED"))
                .patch("/admin/reservations/" + reservationId + "/status");
        this.reservationId = (long) reservationId;
    }

    @Then("예약은 취소 상태다")
    public void 예약은취소상태다() {
        lastResponse.then().body("status", equalTo("CANCELLED"));
    }

}
