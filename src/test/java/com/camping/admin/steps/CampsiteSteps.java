package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.CampsiteApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CampsiteSteps {

    private static final Logger log = LoggerFactory.getLogger(CampsiteSteps.class);

    private final ApiClientContext api;
    private final ScenarioContext scenario;

    private String updatedDescription;
    private int updatedMaxPeople;

    public CampsiteSteps(ApiClientContext api, ScenarioContext scenario) {
        this.api = api;
        this.scenario = scenario;
    }

    private CampsiteApiClient helper() {
        return api.campsite();
    }

    @When("관리자가 새 캠핑장을 등록한다")
    public void 관리자가새캠핑장을등록한다() {
        String siteNumber = "B-" + System.currentTimeMillis();
        scenario.setCreatedSiteNumber(siteNumber);
        scenario.setLastResponse(helper().createCampsite(siteNumber, "테스트 캠핑장", 4));
    }

    @Then("캠핑장이 등록된다")
    public void 캠핑장이등록된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(200, 201);

        Response getResponse = helper().getCampsites();
        String createdSiteNumber = scenario.getCreatedSiteNumber();
        String foundSiteNumber = getResponse.jsonPath()
            .getString("find { it.siteNumber == '" + createdSiteNumber + "' }.siteNumber");
        assertThat(foundSiteNumber).isEqualTo(createdSiteNumber);
        log.info("[Then] 캠핑장이 등록된다 - DB 반영 확인 완료");
    }

    @Given("등록된 캠핑장이 있다")
    public void 등록된캠핑장이있다() {
        Response response = helper().getCampsites();
        response.then().statusCode(200);
        scenario.setTargetCampsiteId(response.jsonPath().getInt("[0].id"));
        log.info("[Given] 등록된 캠핑장이 있다. ID: {}", scenario.getTargetCampsiteId());
    }

    @When("관리자가 캠핑장 정보를 수정한다")
    public void 관리자가캠핑장정보를수정한다() {
        updatedDescription = "수정된 설명";
        updatedMaxPeople = 6;
        scenario.setLastResponse(helper().updateCampsite(
            scenario.getTargetCampsiteId(), updatedDescription, updatedMaxPeople));
    }

    @Then("캠핑장 정보가 수정된다")
    public void 캠핑장정보가수정된다() {
        scenario.getLastResponse().then().statusCode(200);

        Response getResponse = helper().getCampsites();
        int targetCampsiteId = scenario.getTargetCampsiteId();
        String dbDescription = getResponse.jsonPath()
            .getString("find { it.id == " + targetCampsiteId + " }.description");
        int dbMaxPeople = getResponse.jsonPath()
            .getInt("find { it.id == " + targetCampsiteId + " }.maxPeople");

        assertThat(dbDescription).isEqualTo(updatedDescription);
        assertThat(dbMaxPeople).isEqualTo(updatedMaxPeople);
        log.info("[Then] 캠핑장 정보가 수정된다 - DB 반영 확인 완료");
    }

    @Given("사이트 번호 {word} 캠핑장이 있다")
    public void 사이트번호캠핑장이있다(String siteNumber) {
        log.info("[Given] 사이트 번호 {} 캠핑장이 있다", siteNumber);
    }

    @When("관리자가 사이트 번호 {word}로 캠핑장을 등록한다")
    public void 관리자가사이트번호로캠핑장을등록한다(String siteNumber) {
        scenario.setLastResponse(helper().createCampsite(siteNumber, "중복 테스트", 4));
    }

    @Then("캠핑장 등록이 거부된다")
    public void 캠핑장등록이거부된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(400, 409);
        log.info("[Then] 캠핑장 등록이 거부된다");
    }

    @When("관리자가 존재하지 않는 캠핑장을 수정한다")
    public void 관리자가존재하지않는캠핑장을수정한다() {
        scenario.setLastResponse(helper().updateCampsite(99999, "수정된 설명", 6));
    }

    @Then("캠핑장 수정이 거부된다")
    public void 캠핑장수정이거부된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(400, 404, 409);
        log.info("[Then] 캠핑장 수정이 거부된다");
    }
}
