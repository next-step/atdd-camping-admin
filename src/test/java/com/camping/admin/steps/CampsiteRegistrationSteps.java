package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.support.ApiClient;
import com.camping.admin.support.ScenarioContext;
import com.camping.admin.support.TestDataFactory;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


import static org.assertj.core.api.Assertions.assertThat;

public class CampsiteRegistrationSteps {
    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before("@campsite-registration")
    public void cleanupCampsites() {
        long nextId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id), 0) + 1 FROM campsites", Long.class
        );
        jdbcTemplate.execute("ALTER TABLE campsites ALTER COLUMN id RESTART WITH " + nextId);
    }

    // ===== 공통 헬퍼 메서드 =====

    private void requestCampsiteRegistration(CreateCampsiteRequest request) {
        Response response = apiClient.post("/admin/campsites", request);
        scenarioContext.setResponse(response);
    }

    // ==== Given =====
    @Given("사이트 번호 {string}인 캠핑장 사이트가 존재한다")
    public void 사이트_번호_인_캠핑장_사이트가_존재한다(String siteNumber) {
        Campsite campsite = testDataFactory.getCampsiteBySiteNumber(siteNumber);
        scenarioContext.setCampsite(campsite);
    }

    // ===== When =====
    @When("사이트 번호 {string}, 설명 {string}, 최대 인원 {int}명으로 캠핑장 사이트 등록을 요청한다")
    public void 사이트_번호_설명_최대_인원_명으로_캠핑장_사이트_등록을_요청한다(String siteNumber, String description, int maxPeople) {
        CreateCampsiteRequest request = new CreateCampsiteRequest(siteNumber, description, maxPeople);
        requestCampsiteRegistration(request);
    }

    @When("사이트 번호 없이 캠핑장 사이트 등록을 요청한다")
    public void 사이트_번호_없이_캠핑장_사이트_등록을_요청한다() {
        CreateCampsiteRequest request = new CreateCampsiteRequest(null, "테스트", 4);
        requestCampsiteRegistration(request);
    }

    @When("사이트 번호 {string}로 캠핑장 사이트 등록을 요청한다")
    public void 사이트_번호_로_캠핑장_사이트_등록을_요청한다(String siteNumber) {
        CreateCampsiteRequest request = new CreateCampsiteRequest(siteNumber, null, null);
        requestCampsiteRegistration(request);
    }

    @When("최대 인원 {int}명으로 캠핑장 사이트 등록을 요청한다")
    public void 최대_인원_명으로_캠핑장_사이트_등록을_요청한다(int maxPeople) {
        CreateCampsiteRequest request = new CreateCampsiteRequest("AUTO-" + System.currentTimeMillis(), null, maxPeople);
        requestCampsiteRegistration(request);
    }

    // ===== Then =====

    @Then("캠핑장 사이트가 성공적으로 등록된다")
    public void 캠핑장_사이트가_성공적으로_등록된다() {
        Response response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Then("클라이언트 오류로 캠핑장 사이트 등록이 실패한다")
    public void 클라이언트_오류로_캠핑장_사이트_등록이_실패한다() {
        Response response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isIn(400);
    }

    @Then("중복 오류로 캠핑장 사이트 등록이 실패한다")
    public void 중복_오류로_캠핑장_사이트_등록이_실패한다() {
        Response response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isIn(409);
    }

    // ===== And =====

    @And("등록된 사이트의 번호는 {string}이다")
    public void 등록된_사이트의_번호는_이다(String expectedSiteNumber) {
        Response response = scenarioContext.getResponse();
        assertThat(response.jsonPath().getString("siteNumber")).isEqualTo(expectedSiteNumber);
    }

    @And("등록된 사이트의 설명은 {string}이다")
    public void 등록된_사이트의_설명은_이다(String expectedDescription) {
        Response response = scenarioContext.getResponse();
        assertThat(response.jsonPath().getString("description")).isEqualTo(expectedDescription);
    }

    @And("등록된 사이트의 최대 인원은 {int}명이다")
    public void 등록된_사이트의_최대_인원은_명이다(int expectedMaxPeople) {
        Response response = scenarioContext.getResponse();
        assertThat(response.jsonPath().getInt("maxPeople")).isEqualTo(expectedMaxPeople);
    }

    @And("등록된 사이트의 최대 인원은 비어있다")
    public void 등록된_사이트의_최대_인원은_비어있다() {
        Response response = scenarioContext.getResponse();
        assertThat(response.jsonPath().getObject("maxPeople", Integer.class)).isNull();
    }
}
