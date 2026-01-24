package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class ReservationSteps {

    @Given("사용자가 캠핑장 예약을 했다")
    public void 사용자가캠핑장예약을했다() {
      System.out.println(">>> [Given] 사용자가 캠핑장 예약을 하는 단계를 수행합니다.");
    }

    @When("관리자가 해당 예약을 취소하면")
    public void 관리자가해당예약을취소하면() {
      System.out.println(">>> [When] 관리자가해당예약을취소하면");

    }

    @Then("예약이 성공적으로 취소된다")
    public void 예약이성공적으로취소된다() {
      System.out.println(">>> [Then] 예약이성공적으로취소된다");
    }
}