package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.RevenueTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class RevenueSteps {

    private final TestHelperContext helpers;
    private Response lastResponse;

    public RevenueSteps(TestHelperContext helpers) {
        this.helpers = helpers;
    }

    private RevenueTestHelper helper() {
        return helpers.revenue();
    }

    @When("관리자가 오늘 일일 매출을 조회한다")
    public void 관리자가오늘일일매출을조회한다() {
        lastResponse = helper().getTodayRevenue();
    }

    @Then("일일 매출 리포트가 반환된다")
    public void 일일매출리포트가반환된다() {
        lastResponse.then().statusCode(200);
        System.out.println("[Then] 일일 매출 리포트가 반환된다");
    }

    @When("관리자가 이번 달 매출을 조회한다")
    public void 관리자가이번달매출을조회한다() {
        lastResponse = helper().getCurrentMonthRevenue();
    }

    @Then("기간별 매출 리포트가 반환된다")
    public void 기간별매출리포트가반환된다() {
        lastResponse.then().statusCode(200);
        System.out.println("[Then] 기간별 매출 리포트가 반환된다");
    }

    @Given("오늘 판매 내역이 있다")
    public void 오늘판매내역이있다() {
        System.out.println("[Given] 오늘 판매 내역이 있다");
    }

    @Given("오늘 대여 내역이 있다")
    public void 오늘대여내역이있다() {
        System.out.println("[Given] 오늘 대여 내역이 있다");
    }

    @Then("판매와 대여 매출이 합산되어 반환된다")
    public void 판매와대여매출이합산되어반환된다() {
        lastResponse.then().statusCode(200);

        Object grandTotal = lastResponse.jsonPath().get("grandTotalRevenue");
        assertThat(grandTotal).isNotNull();
        System.out.println("[Then] 판매와 대여 매출이 합산되어 반환된다");
    }

    @When("관리자가 데이터가 없는 기간의 매출을 조회한다")
    public void 관리자가데이터가없는기간의매출을조회한다() {
        lastResponse = helper().getRangeRevenue("2000-01-01", "2000-01-31");
    }

    @Then("빈 매출 리포트가 반환된다")
    public void 빈매출리포트가반환된다() {
        lastResponse.then().statusCode(200);
        System.out.println("[Then] 빈 매출 리포트가 반환된다");
    }
}