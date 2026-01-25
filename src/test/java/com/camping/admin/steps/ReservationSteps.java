package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReservationSteps {

    @Given("사용자가 캠핑장 예약을 완료한 상태이다")
    public void 사용자가_캠핑장_예약을_완료한_상태이다() {
        System.out.println(">>> [Given] 사용자가 캠핑장 예약을 하는 단계를 수행합니다.");
    }

    @When("관리자가 해당 예약을 취소한다")
    public void 관리자가_해당_예약을_취소한다() {
        System.out.println(">>> [When] 관리자가 해당 예약을 취소하는 단계를 수행합니다.");
    }

    @Then("예약이 성공적으로 취소된다")
    public void 예약이_성공적으로_취소된다() {
        System.out.println(">>> [Then] 예약이 성공적으로 취소되었는지 확인하는 단계를 수행합니다.");
    }
}
