package com.camping.admin.steps;

import com.camping.admin.api.ReservationApi;
import com.camping.admin.common.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class ReservationSteps {

    // ===== Given =====

    @Given("등록된 예약이 있다")
    public void 등록된_예약이_있다() {
        // data.sql에 있는 예약 사용
        TestContext.setReservationId(1L);
    }

    // ===== When: 인증 O =====

    @When("관리자가 예약 상태를 변경한다")
    public void 관리자가_예약_상태를_변경한다() {
        Response response = ReservationApi.상태_변경(
                TestContext.getAdminToken(),
                TestContext.getReservationId(),
                "CONFIRMED"
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 예약 목록을 조회한다")
    public void 관리자가_예약_목록을_조회한다() {
        Response response = ReservationApi.목록_조회(TestContext.getAdminToken());
        TestContext.setLastResponse(response);
    }

    @When("관리자가 존재하지 않는 예약의 상태를 변경한다")
    public void 관리자가_존재하지_않는_예약의_상태를_변경한다() {
        Response response = ReservationApi.상태_변경(
                TestContext.getAdminToken(),
                99999L,
                "CONFIRMED"
        );
        TestContext.setLastResponse(response);
    }

    // ===== When: 인증 X =====

    @When("관리자 권한 없이 예약 상태를 변경한다")
    public void 관리자_권한_없이_예약_상태를_변경한다() {
        Response response = ReservationApi.상태_변경_인증없이(1L, "CONFIRMED");
        TestContext.setLastResponse(response);
    }

    @When("관리자 권한 없이 예약 목록을 조회한다")
    public void 관리자_권한_없이_예약_목록을_조회한다() {
        Response response = ReservationApi.목록_조회_인증없이();
        TestContext.setLastResponse(response);
    }
}
