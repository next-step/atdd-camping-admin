package com.camping.admin.steps;

import com.camping.admin.api.RevenueApi;
import com.camping.admin.common.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.time.LocalDate;

public class RevenueSteps {

    private static final String TODAY = LocalDate.now().toString();
    private static final String FROM = LocalDate.now().minusDays(30).toString();
    private static final String TO = LocalDate.now().toString();

    // ===== When: 인증 O =====

    @When("관리자가 일별 매출 리포트를 조회한다")
    public void 관리자가_일별_매출_리포트를_조회한다() {
        Response response = RevenueApi.일별_매출_조회(TestContext.getAdminToken(), TODAY);
        TestContext.setLastResponse(response);
    }

    @When("관리자가 기간별 매출 리포트를 조회한다")
    public void 관리자가_기간별_매출_리포트를_조회한다() {
        Response response = RevenueApi.기간별_매출_조회(TestContext.getAdminToken(), FROM, TO);
        TestContext.setLastResponse(response);
    }

    @When("관리자가 기간별 매출 상세 내역을 조회한다")
    public void 관리자가_기간별_매출_상세_내역을_조회한다() {
        Response response = RevenueApi.기간별_매출_상세_조회(TestContext.getAdminToken(), FROM, TO);
        TestContext.setLastResponse(response);
    }

    // ===== When: 인증 X =====

    @When("관리자 권한 없이 일별 매출 리포트를 조회한다")
    public void 관리자_권한_없이_일별_매출_리포트를_조회한다() {
        Response response = RevenueApi.일별_매출_조회_인증없이(TODAY);
        TestContext.setLastResponse(response);
    }

    @When("관리자 권한 없이 기간별 매출 리포트를 조회한다")
    public void 관리자_권한_없이_기간별_매출_리포트를_조회한다() {
        Response response = RevenueApi.기간별_매출_조회_인증없이(FROM, TO);
        TestContext.setLastResponse(response);
    }

    @When("관리자 권한 없이 기간별 매출 상세 내역을 조회한다")
    public void 관리자_권한_없이_기간별_매출_상세_내역을_조회한다() {
        Response response = RevenueApi.기간별_매출_상세_조회_인증없이(FROM, TO);
        TestContext.setLastResponse(response);
    }
}
