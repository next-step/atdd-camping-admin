package com.camping.admin.steps;

import static com.camping.admin.steps.constant.AllContants.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.factory.ReservationFactory;
import com.camping.admin.support.TestApiSupport;
import com.camping.admin.api.TestContext;
import com.camping.admin.support.TestRepositorySupport;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;


public class ReservationSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestContext testContext;

    @Autowired
    private TestApiSupport api;

    @Autowired
    private TestRepositorySupport repository;

    @Autowired
    private ReservationFactory reservationFactory;



    // ==================== Given Steps ====================

    @Given("{string} 고객의 {string} 캠프사이트 예약이 확정된 상태로 존재한다")
    public void 확정된_예약이_존재한다(String customerName, String siteNumber) {
        Reservation saved = reservationFactory.createReservation(customerName, siteNumber, 확정);
        testContext.getReservation().setId(saved.getId());
    }

    @Given("{string} 고객의 {string} 캠프사이트 예약이 대기 상태로 존재한다")
    public void 대기_예약이_존재한다(String customerName, String siteNumber) {
        Reservation saved = reservationFactory.createReservation(customerName, siteNumber, 대기);
        testContext.getReservation().setId(saved.getId());
    }

    @Given("{string} 고객의 {string} 캠프사이트 예약이 보류 상태로 존재한다")
    public void 보류_예약이_존재한다(String customerName, String siteNumber) {
        Reservation saved = reservationFactory.createReservation(customerName, siteNumber, 보류);
        testContext.getReservation().setId(saved.getId());
    }

    @Given("{string} 고객의 {string} 캠프사이트 예약이 취소된 상태로 존재한다")
    public void 취소된_예약이_존재한다(String customerName, String siteNumber) {
        Reservation saved = reservationFactory.createReservation(customerName, siteNumber, 취소);
        testContext.getReservation().setId(saved.getId());
    }

    @Given("{string} 고객의 {string} 캠프사이트 예약이 체크인된 상태로 존재한다")
    public void 체크인된_예약이_존재한다(String customerName, String siteNumber) {
        Reservation saved = reservationFactory.createReservation(customerName, siteNumber, 체크인);
        testContext.getReservation().setId(saved.getId());
    }

    @Given("{string} 고객의 {string} 캠프사이트 예약이 체크아웃된 상태로 존재한다")
    public void 체크아웃된_예약이_존재한다(String customerName, String siteNumber) {
        Reservation saved = reservationFactory.createReservation(customerName, siteNumber, 체크아웃);
        testContext.getReservation().setId(saved.getId());
    }

    @Given("{string} 고객의 {string} 캠프사이트 예약이 거절된 상태로 존재한다")
    public void 거절된_예약이_존재한다(String customerName, String siteNumber) {
        Reservation saved = reservationFactory.createReservation(customerName, siteNumber, 거절);
        testContext.getReservation().setId(saved.getId());
    }

    @Given("예약 ID {string}는 존재하지 않는다")
    public void 존재하지_않는_예약_ID(String reservationId) {
        testContext.getReservation().setId(Long.parseLong(reservationId));
    }

    // ==================== When Steps ====================

    @When("관리자가 해당 예약을 취소한다")
    public void 예약을_취소한다() {
        var response = api.reservation().예약_상태_변경(
                testContext.getAccessToken(), testContext.getReservation().getId(), 취소);
        testContext.setResponse(response);
    }

    @When("관리자가 해당 예약을 확정한다")
    public void 예약을_확정한다() {
        var response = api.reservation().예약_상태_변경(
                testContext.getAccessToken(), testContext.getReservation().getId(), 확정);
        testContext.setResponse(response);
    }

    @When("관리자가 해당 예약의 상태를 빈 값으로 변경 요청한다")
    public void 빈_값으로_상태_변경() {
        var response = api.reservation().예약_상태_변경(
                testContext.getAccessToken(), testContext.getReservation().getId(), "");
        testContext.setResponse(response);
    }

    @When("관리자가 해당 예약의 상태를 본문 없이 변경 요청한다")
    public void 본문_없이_상태_변경() {
        var response = api.reservation().예약_상태_변경_본문없이(
                testContext.getAccessToken(), testContext.getReservation().getId());
        testContext.setResponse(response);
    }

    // ==================== Then Steps ====================

    @Then("해당 예약의 상태가 취소로 변경된다")
    public void 예약_상태가_취소로_변경된다() {
        assertThat(testContext.getResponse().statusCode())
                .as("API 응답 상태코드")
                .isEqualTo(HttpStatus.OK.value());

        assertThat(testContext.getResponse().jsonPath().getString("status"))
                .as("API 응답의 예약 상태")
                .isEqualTo(취소);

        Reservation reservation = repository.reservation()
                .findById(testContext.getReservation().getId())
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다"));

        assertThat(reservation.getStatus().name())
                .as("DB에 저장된 예약 상태")
                .isEqualTo(취소);
    }

    @Then("이미 취소된 예약이라는 오류가 발생한다")
    public void 이미_취소된_예약_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, 이미_취소된_예약);
    }

    @Then("체크인된 예약은 취소할 수 없다는 오류가 발생한다")
    public void 체크인_예약_취소_불가_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, 체크인된_예약);
    }

    @Then("이미 완료된 예약은 취소할 수 없다는 오류가 발생한다")
    public void 완료된_예약_취소_불가_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, 완료된_예약);
    }

    @Then("이미 거절된 예약은 취소할 수 없다는 오류가 발생한다")
    public void 거절된_예약_취소_불가_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, 거절된_예약);
    }

    @Then("예약을 찾을 수 없다는 오류가 발생한다")
    public void 예약_찾을수_없음_오류() {
        assertErrorResponse(HttpStatus.NOT_FOUND, 찾을_수_없다);
    }

    @Then("상태값은 필수라는 오류가 발생한다")
    public void 상태값_필수_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, 필수);
    }

    @Then("잘못된 요청이라는 오류가 발생한다")
    public void 잘못된_요청_오류() {
        assertThat(testContext.getResponse().statusCode())
                .as("API 응답 상태코드")
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Then("취소된 예약은 복구할 수 없다는 오류가 발생한다")
    public void 취소된_예약_복구_불가_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, "복구할 수 없");
    }


    private void assertErrorResponse(HttpStatus expectedStatus, String expectedMessagePart) {
        assertThat(testContext.getResponse().statusCode())
                .as("API 응답 상태코드")
                .isEqualTo(expectedStatus.value());

        String responseBody = testContext.getResponse().body().asString();
        assertThat(responseBody)
                .as("오류 메시지 포함 여부")
                .containsIgnoringCase(expectedMessagePart);
    }
}