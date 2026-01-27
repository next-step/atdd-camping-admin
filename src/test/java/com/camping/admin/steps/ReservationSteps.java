package com.camping.admin.steps;

import com.camping.admin.client.ReservationAdminClient;
import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.fatory.CampsiteTestdataFactory;
import com.camping.admin.fatory.ReservationTestdataFactory;
import com.camping.admin.utils.DatabaseCleaner;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.camping.admin.domain.enums.ReservationStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ReservationAdminClient reservationAdminClient;

    @Autowired
    private CampsiteTestdataFactory campsiteTestdataFactory;

    @Autowired
    private ReservationTestdataFactory reservationTestdataFactory;

    @Before(order = 2)
    public void setUp() {
        databaseCleaner.execute();
    }

    // ==========================================
    // Given - 사전 조건 준비
    // ==========================================

    @Given("캠핑장에 {string} 사이트가 등록되어 있다")
    public void 캠핑장에_사이트가_등록되어_있다(String siteNumber) {
        Campsite campsite = campsiteTestdataFactory.createCampsite(siteNumber);
        testContext.addCampsite(siteNumber, campsite);
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string}이 대기 상태로 예약되어 있다")
    public void 캠핑장에_대기중인_예약이_있다(String siteNumber, String customerName) {
        Campsite campsite = testContext.getCampsite(siteNumber);
        Reservation reservation = reservationTestdataFactory.createReservationWithStatus(customerName, campsite, PENDING.name());
        testContext.addReservation(customerName, reservation);
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string} 이름으로 취소 상태의 예약이 있다")
    public void 캠핑장에_취소된_예약이_있다(String siteNumber, String customerName) {
        Campsite campsite = testContext.getCampsite(siteNumber);
        Reservation reservation = reservationTestdataFactory.createReservationWithStatus(customerName, campsite, CANCELLED.name());
        testContext.addReservation(customerName, reservation);
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string} 이름으로 확정된 예약이 있다")
    public void 캠핑장에_확정된_예약이_있다(String siteNumber, String customerName) {
        Campsite campsite = testContext.getCampsite(siteNumber);
        Reservation reservation = reservationTestdataFactory.createReservationWithStatus(customerName, campsite, CONFIRMED.name());
        testContext.addReservation(customerName, reservation);
    }

    // ==========================================
    // When - API 행위 실행
    // ==========================================

    @When("관리자가 {string}의 예약을 확정하면")
    public void 관리자가_고객의_예약을_확정하면(String customerName) {
        Map<String, Object> requestBody = Map.of("status", CONFIRMED.name());
        var reservationId = testContext.getReservation(customerName).getId();
        var authToken = testContext.getAuthToken();
        ExtractableResponse<Response> response = reservationAdminClient.예약_상태를_변경한다(authToken, reservationId, requestBody);
        testContext.setResponse(response);
    }

    @When("관리자가 존재하지 않는 예약\\(ID {long}\\)의 상태를 확정하려고 하면")
    public void 관리자가_존재하지_않는_예약의_상태를_확정하려고_하면(long reservationId) {
        Map<String, Object> requestBody = Map.of("status", CONFIRMED.name());
        var authToken = testContext.getAuthToken();
        ExtractableResponse<Response> response = reservationAdminClient.예약_상태를_변경한다(authToken, reservationId, requestBody);
        testContext.setResponse(response);
    }

    @When("관리자가 잘못된 본문\\({string}\\)으로 {string}의 예약 상태 변경을 요청하면")
    public void 관리자가_잘못된_본문으로_예약_상태를_변경을_요청하면(String invalidBody, String customerName) {
        var reservationId = testContext.getReservation(customerName).getId();
        var authToken = testContext.getAuthToken();
        ExtractableResponse<Response> response = reservationAdminClient.잘못된_본문으로_예약_상태를_변경한다(authToken, reservationId, invalidBody);
        testContext.setResponse(response);
    }

    @When("관리자가 예약 목록을 조회하면")
    public void 관리자가_예약_목록을_조회하면() {
        var authToken = testContext.getAuthToken();
        ExtractableResponse<Response> response = reservationAdminClient.예약_목록을_조회한다(authToken);
        testContext.setResponse(response);
    }

    // ==========================================
    // Then - 검증
    // ==========================================

    @Then("요청이 성공한다")
    public void 요청이_성공한다() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @And("예약이 확정된다")
    public void 예약이_확정된다() {
        var response = testContext.getResponse();
        String actualStatus = response.jsonPath().getString("status");
        assertThat(actualStatus).isEqualTo(CONFIRMED.name());
    }

    @Then("요청이 실패한다\\({int}\\)")
    public void 요청이_실패한다(int expectedStatusCode) {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(expectedStatusCode);
    }

    @Then("전체 예약 목록의 개수는 {int}개이다")
    public void 전체_예약_목록의_개수는_N개이다(int count) {
        var response = testContext.getResponse();
        assertThat(response.jsonPath().getList("$")).hasSize(count);
    }
}
