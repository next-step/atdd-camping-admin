package com.camping.admin.steps;

import com.camping.admin.steps.api.CampsiteApi;
import com.camping.admin.steps.context.TestContext;
import com.camping.admin.steps.support.TestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
public class CampsiteSteps {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private TestContext testContext;

    @Autowired
    private CampsiteApi campsiteApi;

    @When("관리자가 사이트 번호 {string}, 설명 {string}, 최대 인원 {int}인 캠프사이트를 등록하면")
    public void adminCreatesCampsite(String siteNumber, String description, int maxPeople) {
        if ("null".equals(siteNumber)) {
            siteNumber = null;
        }
        testContext.setLastResponse(campsiteApi.캠프사이트_생성_요청(testContext.getAdminToken(), siteNumber, description, maxPeople));
    }

    @Then("캠프사이트가 성공적으로 등록된다")
    public void campsiteIsCreated() {
        assertThat(testContext.getLastResponse().statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Then("캠프사이트 등록이 실패한다")
    public void campsiteCreationShouldFail() {
        assertThat(testContext.getLastResponse().statusCode()).isGreaterThanOrEqualTo(400);
    }

    @And("등록된 캠프사이트의 번호는 {string}, 설명은 {string}, 최대 인원은 {int}이어야 한다")
    public void campsiteDetailsShouldBe(String siteNumber, String description, int maxPeople) {
        ExtractableResponse<Response> response = campsiteApi.캠프사이트_목록_조회_요청(testContext.getAdminToken());
        List<Map<String, Object>> campsites = response.jsonPath().getList("");

        Map<String, Object> campsite = campsites.stream()
                .filter(c -> c.get("siteNumber").equals(siteNumber))
                .findFirst()
                .orElseThrow();

        assertThat(campsite.get("siteNumber")).isEqualTo(siteNumber);
        assertThat(campsite.get("description")).isEqualTo(description);
        assertThat(campsite.get("maxPeople")).isEqualTo(maxPeople);
    }

    @Given("{string} 사이트 번호를 가진 캠프사이트가 이미 등록되어 있다")
    public void campsiteWithSiteNumberAlreadyExists(String siteNumber) {
        testDataFactory.createCampsite(siteNumber, "Existing Site", 4);
    }
}
