package com.camping.admin.steps;

import com.camping.admin.CucumberSpringConfiguration;
import com.camping.admin.helper.CampsiteTestHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 캠프사이트 생성 기능의 인수 테스트 Step 정의
 */
public class CampsiteSteps extends CucumberSpringConfiguration {

    @Autowired
    private CampsiteTestHelper helper;

    // ==================== Given ====================

    @Given("사이트번호가 {string}인 캠프사이트가 이미 존재한다")
    public void 사이트번호가_인_캠프사이트가_이미_존재한다(String siteNumber) {
        helper.사이트번호로_캠프사이트를_생성한다(siteNumber);
    }

    // ==================== When ====================

    @When("관리자가 다음 정보로 캠프사이트를 등록한다")
    public void 관리자가_다음_정보로_캠프사이트를_등록한다(DataTable dataTable) {
        helper.캠프사이트_정보로_등록을_요청한다(dataTable);
    }

    @When("관리자가 사이트번호 없이 캠프사이트 등록을 요청한다")
    public void 관리자가_사이트번호_없이_캠프사이트_등록을_요청한다() {
        helper.사이트번호_없이_등록을_요청한다();
    }

    @When("관리자가 {string} 사이트번호로 캠프사이트 등록을 요청한다")
    public void 관리자가_사이트번호로_캠프사이트_등록을_요청한다(String siteNumber) {
        helper.사이트번호로_등록을_요청한다(siteNumber);
    }

    @When("관리자가 최대인원을 {int}명으로 캠프사이트 등록을 요청한다")
    public void 관리자가_최대인원을_N명으로_캠프사이트_등록을_요청한다(int maxPeople) {
        helper.최대인원으로_등록을_요청한다(maxPeople);
    }

    @When("관리자가 사이트번호를 빈 값으로 캠프사이트 등록을 요청한다")
    public void 관리자가_사이트번호를_빈_값으로_캠프사이트_등록을_요청한다() {
        helper.빈_사이트번호로_등록을_요청한다();
    }

    // ==================== Then ====================

    @Then("캠프사이트가 등록된다")
    public void 캠프사이트가_등록된다() {
        helper.캠프사이트가_등록되었는지_검증한다();
    }

    @Then("캠프사이트는 등록되지 않는다")
    public void 캠프사이트는_등록되지_않는다() {
        helper.캠프사이트가_등록되지_않았는지_검증한다();
    }

    @Then("사이트번호가 {string}인 캠프사이트가 생성된다")
    public void 사이트번호가_인_캠프사이트가_생성된다(String siteNumber) {
        helper.사이트번호로_캠프사이트가_존재하는지_검증한다(siteNumber);
    }
}