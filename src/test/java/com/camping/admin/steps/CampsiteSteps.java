package com.camping.admin.steps;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

import com.camping.admin.context.CommonContext;
import com.camping.admin.helper.TestApiHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CampsiteSteps {
    private final CommonContext commonContext = new CommonContext();

    @Given("{string} 사이트 번호로 캠프사이트가 이미 존재한다")
    public void campsiteWithSiteNumberAlreadyExists(String siteNumber) {
        commonContext.setResponse(
                TestApiHelper.sendCampsiteCreationRequest(siteNumber)
        );
        assertSuccessfulCreation();
    }

    @When("사이트 번호 {string}로 캠프사이트 생성을 요청한다")
    public void requestCampsiteCreation(String siteNumber) {
        commonContext.setResponse(
                TestApiHelper.sendCampsiteCreationRequest(siteNumber)
        );
    }

    @When("사이트 번호 없이 캠프사이트 생성을 요청한다")
    public void requestCampsiteCreationWithoutSiteNumber() {
        commonContext.setResponse(
                TestApiHelper.sendCampsiteCreationRequest(null)
        );
    }

    @When("{string} 사이트를 {string} 사이트 번호로 변경 요청한다")
    public void requestCampsiteSiteNumberUpdate(String oldSiteNumber, String newSiteNumber) {
        Long campsiteId = TestApiHelper.findCampsiteIdBySiteNumber(oldSiteNumber);

        commonContext.setResponse(
                TestApiHelper.sendCampsiteUpdateRequest(campsiteId, newSiteNumber, "수정된 설명", 4)
        );
    }

    @Then("캠프사이트가 성공적으로 생성된다")
    public void assertSuccessfulCreation() {
        commonContext.getResponse().then()
                .statusCode(201)
                .body(notNullValue());
    }

    @Then("요청이 실패하고 오류 메시지를 받는다")
    public void assertFailedRequest() {
        commonContext.getResponse().then()
                .statusCode(greaterThanOrEqualTo(400))
                .statusCode(lessThan(500))
                .body("message", notNullValue());
    }

    @Then("캠프사이트 정보가 성공적으로 수정된다")
    public void assertSuccessfulUpdate() {
        commonContext.getResponse().then().statusCode(200);
    }
}
