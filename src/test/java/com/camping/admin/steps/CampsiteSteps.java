package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.CampsiteTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class CampsiteSteps {

    private final TestHelperContext helpers;
    private Response lastResponse;
    private int targetCampsiteId;
    private String createdSiteNumber;
    private String updatedDescription;
    private int updatedMaxPeople;

    public CampsiteSteps(TestHelperContext helpers) {
        this.helpers = helpers;
    }

    private CampsiteTestHelper helper() {
        return helpers.campsite();
    }

    @When("관리자가 새 캠핑장을 등록한다")
    public void 관리자가새캠핑장을등록한다() {
        createdSiteNumber = "B-" + System.currentTimeMillis();
        lastResponse = helper().createCampsite(createdSiteNumber, "테스트 캠핑장", 4);
    }

    @Then("캠핑장이 등록된다")
    public void 캠핑장이등록된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(200, 201);

        Response getResponse = helper().getCampsites();
        String foundSiteNumber = getResponse.jsonPath()
            .getString("find { it.siteNumber == '" + createdSiteNumber + "' }.siteNumber");
        assertThat(foundSiteNumber).isEqualTo(createdSiteNumber);
        System.out.println("[Then] 캠핑장이 등록된다 - DB 반영 확인 완료");
    }

    @Given("등록된 캠핑장이 있다")
    public void 등록된캠핑장이있다() {
        Response response = helper().getCampsites();
        response.then().statusCode(200);
        targetCampsiteId = response.jsonPath().getInt("[0].id");
        System.out.println("[Given] 등록된 캠핑장이 있다. ID: " + targetCampsiteId);
    }

    @When("관리자가 캠핑장 정보를 수정한다")
    public void 관리자가캠핑장정보를수정한다() {
        updatedDescription = "수정된 설명";
        updatedMaxPeople = 6;
        lastResponse = helper().updateCampsite(targetCampsiteId, updatedDescription, updatedMaxPeople);
    }

    @Then("캠핑장 정보가 수정된다")
    public void 캠핑장정보가수정된다() {
        lastResponse.then().statusCode(200);

        Response getResponse = helper().getCampsites();
        String dbDescription = getResponse.jsonPath()
            .getString("find { it.id == " + targetCampsiteId + " }.description");
        int dbMaxPeople = getResponse.jsonPath()
            .getInt("find { it.id == " + targetCampsiteId + " }.maxPeople");

        assertThat(dbDescription).isEqualTo(updatedDescription);
        assertThat(dbMaxPeople).isEqualTo(updatedMaxPeople);
        System.out.println("[Then] 캠핑장 정보가 수정된다 - DB 반영 확인 완료");
    }

    @Given("사이트 번호 {word} 캠핑장이 있다")
    public void 사이트번호캠핑장이있다(String siteNumber) {
        System.out.println("[Given] 사이트 번호 " + siteNumber + " 캠핑장이 있다");
    }

    @When("관리자가 사이트 번호 {word}로 캠핑장을 등록한다")
    public void 관리자가사이트번호로캠핑장을등록한다(String siteNumber) {
        lastResponse = helper().createCampsite(siteNumber, "중복 테스트", 4);
    }

    @Then("캠핑장 등록이 거부된다")
    public void 캠핑장등록이거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 409);
        System.out.println("[Then] 캠핑장 등록이 거부된다");
    }

    @When("관리자가 존재하지 않는 캠핑장을 수정한다")
    public void 관리자가존재하지않는캠핑장을수정한다() {
        lastResponse = helper().updateCampsite(99999, "수정된 설명", 6);
    }

    @Then("캠핑장 수정이 거부된다")
    public void 캠핑장수정이거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 404, 409);
        System.out.println("[Then] 캠핑장 수정이 거부된다");
    }
}