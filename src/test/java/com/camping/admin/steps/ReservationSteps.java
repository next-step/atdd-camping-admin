package com.camping.admin.steps;

import com.camping.admin.client.AuthClient;
import com.camping.admin.client.ReservationAdminClient;
import com.camping.admin.common.DatabaseCleaner;
import com.camping.admin.support.CampsiteSupport;
import com.camping.admin.support.ReservationSupport;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestContext testContext;

    @Autowired
    private CampsiteSupport campsiteSupport;

    @Autowired
    private ReservationSupport reservationSupport;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private ReservationAdminClient reservationAdminClient;

    @Before(order = 2)
    public void setUp() {
        databaseCleaner.execute();
    }

    // ==========================================
    // Given - 사전 조건 준비
    // ==========================================

    @Given("캠핑장에 {string} 사이트가 등록되어 있다")
    public void 캠핑장에_사이트가_등록되어_있다(String siteNumber) {
        campsiteSupport.캠핑장_사이트가_등록되어_있다(siteNumber);
    }

    @Given("관리자 로그인이 되어 있다")
    public void 관리자_로그인이_되어있다() {
        authClient.관리자_로그인이_되어있다();
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string} 이름으로 예약되어 있다")
    public void 캠핑장에_예약이_되어있다(String customerName, String siteNumber) {
        reservationSupport.캠핑장에_예약이_되어있다(customerName);
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string} 이름으로 취소 상태의 예약이 있다")
    public void 캠핑장에_특정_상태의_예약이_있다(String siteNumber, String customerName) {
        reservationSupport.캠핑장에_예약이_되어있다(customerName, "CANCELLED");
    }

    // ==========================================
    // When - API 행위 실행
    // ==========================================

    @When("관리자가 예약을 확정하면")
    public void 관리자가_예약을_확정한다() {
        reservationAdminClient.예약_상태를_변경한다(Map.of("status", "CONFIRMED"));
    }

    @When("관리자가 존재하지 않는 예약\\(ID {long}\\)의 상태를 확정하려고 하면")
    public void 관리자가_존재하지_않는_예약의_상태를_변경한다(long reservationId) {
        reservationAdminClient.예약_상태를_변경한다(reservationId, Map.of("status", "CONFIRMED"));
    }

    @When("관리자가 예약 상태를 변경할 때 잘못된 본문\\({string}\\)으로 요청하면")
    public void 관리자가_잘못된_본문으로_예약_상태를_변경하면(String invalidBody) {
        reservationAdminClient.예약_상태를_변경한다(invalidBody);
    }

    // ==========================================
    // Then - 검증
    // ==========================================

    @Then("요청이 성공한다")
    public void 요청이_성공한다() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @And("예약의 상태가 확정된다")
    public void 예약_상태가_변경되어_있다() {
        var response = testContext.getResponse();
        String actualStatus = response.jsonPath().getString("status");
        assertThat(actualStatus).isEqualTo("CONFIRMED"); // TODO. 상수
    }

    @Then("요청이 실패한다\\({int}\\)")
    public void 요청이_실패한다(int expectedStatusCode) {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(expectedStatusCode);
    }

}
