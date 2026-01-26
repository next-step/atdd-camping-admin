package com.camping.admin.steps;

import com.camping.admin.api.ReservationApi;
import com.camping.admin.common.TestContext;
import com.camping.admin.common.TestData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    @Given("사용자가 캠핑장 예약을 했다")
    public void 사용자가_캠핑장_예약을_했다() {
        TestContext.setReservationId(TestData.RESERVATION_HONG_ID);
    }

    @When("관리자가 해당 예약을 취소하면")
    public void 관리자가_해당_예약을_취소하면() {
        Response response = ReservationApi.예약을_취소한다(
                TestContext.getAdminToken(),
                TestContext.getReservationId()
        );
        TestContext.setLastResponse(response);
    }

    @Then("예약이 성공적으로 취소된다")
    public void 예약이_성공적으로_취소된다() {
        Response response = TestContext.getLastResponse();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo("CANCELLED");
    }

    @And("관리자가 이미 해당 예약을 취소했다")
    public void 관리자가_이미_해당_예약을_취소했다() {
        ReservationApi.예약을_취소한다(
                TestContext.getAdminToken(),
                TestContext.getReservationId()
        );
    }

    @When("관리자가 다시 해당 예약을 취소하면")
    public void 관리자가_다시_해당_예약을_취소하면() {
        Response response = ReservationApi.예약을_취소한다(
                TestContext.getAdminToken(),
                TestContext.getReservationId()
        );
        TestContext.setLastResponse(response);
    }

    @Then("시스템 정책에 맞는 결과가 반환된다")
    public void 시스템_정책에_맞는_결과가_반환된다() {
        Response response = TestContext.getLastResponse();
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
