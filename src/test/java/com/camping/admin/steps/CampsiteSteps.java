package com.camping.admin.steps;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

import com.camping.admin.context.CommonContext;
import com.camping.admin.helper.TestApiHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CampsiteSteps {
    private final CommonContext commonContext = new CommonContext();

    @When("사이트 번호 {string}로 캠프사이트 생성을 요청한다")
    public void requestCampsiteCreation(String siteNumber) {
        commonContext.setResponse(
            TestApiHelper.sendCampsiteCreationRequest(siteNumber, "기본 캠프사이트", 4)
        );
    }

    @When("사이트 번호 {string}, 설명 {string}, 최대 인원 {int}명으로 캠프사이트 생성을 요청한다")
    public void requestCampsiteCreationWithDetails(String siteNumber, String description, int maxPeople) {
        commonContext.setResponse(
            TestApiHelper.sendCampsiteCreationRequest(siteNumber, description, maxPeople)
        );
    }

    @Given("{string} 사이트 번호로 캠프사이트가 이미 존재한다")
    public void campsiteWithSiteNumberAlreadyExists(String siteNumber) {
        commonContext.setResponse(
            TestApiHelper.sendCampsiteCreationRequest(siteNumber, "기존 캠프사이트", 4)
        );
        assertSuccessfulCreation();
    }

    @Then("캠프사이트가 성공적으로 생성된다")
    public void assertSuccessfulCreation() {
        commonContext.getResponse().then()
                .statusCode(201);
    }

    @Then("캠프사이트 생성에 실패하고 오류 메시지를 받는다")
    public void assertFailedCreation() {
        commonContext.getResponse().then()
                .statusCode(greaterThanOrEqualTo(400))
                .statusCode(lessThan(500))
                .body("message", notNullValue());
    }

    @And("생성된 캠프사이트의 상세 정보는 사이트 번호 {string}, 설명 {string}, 최대 인원 {int}명이다")
    public void assertCampsiteDetails(String siteNumber, String description, int maxPeople) {
        commonContext.getResponse().then()
                .body("siteNumber", equalTo(siteNumber))
                .body("description", equalTo(description))
                .body("maxPeople", equalTo(maxPeople));
    }
}
