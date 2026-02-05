package com.camping.admin.steps;

import com.camping.admin.CucumberSpringConfiguration;
import com.camping.admin.helper.ReservationTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 예약 관리 기능의 인수 테스트 Step 정의
 */
public class ReservationSteps extends CucumberSpringConfiguration {

    @Autowired
    private ReservationTestHelper helper;

    private static final Long 존재하지_않는_예약_ID = 999999L;
    private static final String EMPTY = "";

    // ==================== Given ====================

    @Given("사이트번호가 {string}인 캠프사이트가 존재한다")
    public void 사이트번호가_인_캠프사이트가_존재한다(String siteNumber) {
        helper.사이트번호로_캠프사이트를_준비한다(siteNumber);
    }

    @Given("해당 캠프사이트에 {string}부터 {string}까지 예약이 존재한다")
    public void 해당_캠프사이트에_부터_까지_예약이_존재한다(String startDate, String endDate) {
        helper.해당_캠프사이트에_예약을_생성한다(startDate, endDate);
    }

    // ==================== When - 예약 생성 ====================

    @When("관리자가 고객명 {string}으로 해당 캠프사이트에 {string}부터 {string}까지 예약을 생성한다")
    public void 관리자가_고객명으로_해당_캠프사이트에_예약을_생성한다(String customerName, String startDate, String endDate) {
        helper.고객명으로_예약을_요청한다(customerName, startDate, endDate);
    }

    @When("관리자가 존재하지 않는 캠프사이트에 예약을 생성한다")
    public void 관리자가_존재하지_않는_캠프사이트에_예약을_생성한다() {
        helper.존재하지_않는_캠프사이트에_예약을_요청한다();
    }

    @When("관리자가 해당 캠프사이트에 {string}부터 {string}까지 예약을 생성한다")
    public void 관리자가_해당_캠프사이트에_예약을_생성한다(String startDate, String endDate) {
        helper.해당_캠프사이트에_날짜로_예약을_요청한다(startDate, endDate);
    }

    @When("관리자가 고객명 없이 해당 캠프사이트에 예약을 생성한다")
    public void 관리자가_고객명_없이_해당_캠프사이트에_예약을_생성한다() {
        helper.고객명_없이_예약을_요청한다();
    }

    // ==================== When - 예약 상태 변경 ====================

    @When("관리자가 확정된 예약을 취소한다")
    public void 관리자가_확정된_예약을_취소한다() {
        helper.확정된_예약을_찾는다();
        helper.현재_예약의_상태를_변경한다("CANCELLED");
    }

    @When("관리자가 확정된 예약을 {예약상태} 상태로 변경한다")
    public void 관리자가_확정된_예약을_상태로_변경한다(String status) {
        helper.확정된_예약을_찾는다();
        helper.현재_예약의_상태를_변경한다(status);
    }

    @When("관리자가 존재하지 않는 예약을 {예약상태} 상태로 변경한다")
    public void 관리자가_존재하지_않는_예약을_상태로_변경한다(String status) {
        helper.예약_상태를_변경한다(존재하지_않는_예약_ID, status);
    }

    @When("관리자가 해당 예약을 {예약상태} 상태로 변경한다")
    public void 관리자가_해당_예약을_상태로_변경한다(String status) {
        helper.현재_예약의_상태를_변경한다(status);
    }

    @When("관리자가 확정된 예약의 상태값을 빈 값으로 예약 상태 변경을 요청한다")
    public void 관리자가_확정된_예약의_상태값을_빈_값으로_예약_상태_변경을_요청한다() {
        helper.확정된_예약을_찾는다();
        helper.현재_예약의_상태를_변경한다(EMPTY);
    }

    // ==================== Then - 예약 생성 ====================

    @Then("예약이 생성된다")
    public void 예약이_생성된다() {
        helper.예약이_생성되었는지_검증한다();
    }

    @Then("예약을 조회하면 고객명이 {string}이다")
    public void 예약을_조회하면_고객명이_이다(String expectedCustomerName) {
        helper.예약을_조회하여_고객명을_검증한다(expectedCustomerName);
    }

    // ==================== Then - 예약 상태 변경 ====================

    @Then("예약을 조회하면 취소 상태이다")
    public void 예약을_조회하면_취소_상태이다() {
        helper.예약을_조회하여_상태를_검증한다("CANCELLED");
    }

    @Then("예약이 {예약상태}된다")
    public void 예약이_된다(String status) {
        helper.예약_상태를_검증한다(status);
    }

    @Then("해당 캠프사이트는 같은 날짜에 다시 예약이 가능하다")
    public void 해당_캠프사이트는_같은_날짜에_다시_예약이_가능하다() {
        helper.겹치는_예약이_없는지_검증한다();
    }

    @Then("예약은 {예약상태} 상태로 유지된다")
    public void 예약은_상태로_유지된다(String status) {
        helper.예약_상태를_검증한다(status);
    }

}
