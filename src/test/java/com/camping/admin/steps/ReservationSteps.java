package com.camping.admin.steps;

import com.camping.admin.domain.enums.ReservationStatus;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 예약 상태 변경 기능의 인수 테스트 Step 정의
 */
public class ReservationSteps extends CucumberSpringConfiguration {

    @Autowired
    private ReservationTestHelper helper;

    private static final Long 존재하지_않는_예약_ID = 999999L;
    private static final String EMPTY = "";

    // ==================== When ====================

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

    // ==================== Then ====================

    @Then("응답 상태 코드는 {int}이다")
    public void 응답_상태_코드는_N이다(int expectedStatusCode) {
        helper.응답_상태_코드를_검증한다(expectedStatusCode);
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