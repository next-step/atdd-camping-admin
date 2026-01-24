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
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    @LocalServerPort
    private int port;

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

    @Before
    public void setUp() {
        RestAssured.port = port;

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

    // ==========================================
    // When - API 행위 실행
    // ==========================================

    @When("관리자가 예약을 {string} 상태로 변경하면")
    public void 관리자가_예약_상태를_변경한다(String status) {
        reservationAdminClient.예약_상태를_변경한다(status);
    }

    @When("관리자가 존재하지 않는 예약\\(ID {long}\\)의 상태를 {string}으로 변경하면")
    public void 관리자가_존재하지_않는_예약의_상태를_변경한다(long reservationId, String status) {
        reservationAdminClient.예약_상태를_변경한다(reservationId, Map.of("status", status));
    }

    @When("관리자가 빈 본문으로 예약 상태 변경을 요청하면")
    public void 관리자가_잘못된_본문으로_예약_상태_변경을_요청한다() {
        reservationAdminClient.예약_상태를_변경한다(Collections.EMPTY_MAP);
    }

    // ==========================================
    // Then - 검증
    // ==========================================

    @Then("요청이 성공한다")
    public void 요청이_성공한다() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @And("그리고 예약 상태가 {string} 으로 변경되어 있다")
    public void 예약_상태가_변경되어_있다(String expectedStatus) {
        var response = testContext.getResponse();
        String actualStatus = response.jsonPath().getString("status");
        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @Then("요청이 {int} 상태 코드로 실패한다")
    public void 요청이_실패한다(int expectedStatusCode) {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(expectedStatusCode);
    }
}
