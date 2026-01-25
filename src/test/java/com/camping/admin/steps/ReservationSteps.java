package com.camping.admin.steps;

import com.camping.admin.api.AuthApi;
import com.camping.admin.api.ReservationApi;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    private Long savedReservationId;
    private Response lastResponse;
    private String adminToken;

    @Before(order = 1)
    public void setupAdminToken() {
        adminToken = AuthApi.관리자_토큰을_발급한다();
    }

    @Given("사용자가 캠핑장 예약을 했다")
    public void 사용자가_캠핑장_예약을_했다() {
        // data.sql: ID=1, 홍길동, CONFIRMED
        this.savedReservationId = 1L;
    }

    @When("관리자가 해당 예약을 취소하면")
    public void 관리자가_해당_예약을_취소하면() {
        lastResponse = ReservationApi.예약을_취소한다(adminToken, savedReservationId);
    }

    @Then("예약이 성공적으로 취소된다")
    public void 예약이_성공적으로_취소된다() {
        assertThat(lastResponse.statusCode()).isEqualTo(200);
        assertThat(lastResponse.jsonPath().getString("status")).isEqualTo("CANCELLED");
    }

    @And("관리자가 이미 해당 예약을 취소했다")
    public void 관리자가_이미_해당_예약을_취소했다() {
        ReservationApi.예약을_취소한다(adminToken, savedReservationId);
    }

    @When("관리자가 다시 해당 예약을 취소하면")
    public void 관리자가_다시_해당_예약을_취소하면() {
        lastResponse = ReservationApi.예약을_취소한다(adminToken, savedReservationId);
    }

    @Then("시스템 정책에 맞는 결과가 반환된다")
    public void 시스템_정책에_맞는_결과가_반환된다() {
        // 현재 시스템 정책: 이미 취소된 예약을 다시 취소해도 200 OK 반환
        assertThat(lastResponse.statusCode()).isEqualTo(200);
    }
}
