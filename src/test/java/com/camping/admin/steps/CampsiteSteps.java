package com.camping.admin.steps;

import com.camping.admin.helpers.ApiHelper;
import com.camping.admin.helpers.CampsiteTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

public class CampsiteSteps {

    @Given("캠프사이트가 등록되어 있다")
    public void campsiteExists() {
        String uniqueSiteNumber = "TEST-" + System.currentTimeMillis();
        Long campsiteId = CampsiteTestHelper.createAndGetCampsiteId(uniqueSiteNumber, "기존 캠프사이트", 4);
        CampsiteTestHelper.setCampsiteId(campsiteId);
    }

    @Given("캠프사이트가 {int}개 등록되어 있다")
    public void multipleCampsitesExist(int count) {
        long timestamp = System.currentTimeMillis();
        for (int i = 1; i <= count; i++) {
            CampsiteTestHelper.createCampsite("TEST-SITE-" + timestamp + "-" + i, "캠프사이트 " + i, 4);
        }
    }

    @When("관리자가 새로운 캠프사이트를 등록한다")
    public void createNewCampsite() {
        String uniqueSiteNumber = "NEW-SITE-" + System.currentTimeMillis();
        Response response = CampsiteTestHelper.createCampsite(uniqueSiteNumber, "신규 캠프사이트", 6);
        CampsiteTestHelper.setResponse(response);
    }

    @When("관리자가 사이트 번호 없이 캠프사이트를 등록한다")
    public void createCampsiteWithoutSiteNumber() {
        Response response = CampsiteTestHelper.createCampsiteWithoutSiteNumber("사이트 번호 없음", 4);
        CampsiteTestHelper.setResponse(response);
    }

    @When("관리자가 캠프사이트 목록을 조회한다")
    public void getAllCampsites() {
        Response response = CampsiteTestHelper.getAllCampsites();
        CampsiteTestHelper.setResponse(response);
    }

    @When("관리자가 캠프사이트 정보를 수정한다")
    public void updateCampsite() {
        Long campsiteId = CampsiteTestHelper.getCampsiteId();
        String uniqueSiteNumber = "UPDATED-" + System.currentTimeMillis();
        Response response = CampsiteTestHelper.updateCampsite(campsiteId, uniqueSiteNumber, "수정된 사이트", 8);
        CampsiteTestHelper.setResponse(response);
    }

    @Then("캠프사이트 등록에 성공한다")
    public void campsiteCreatedSuccessfully() {
        CampsiteTestHelper.getLastResponse().then().statusCode(201);
    }

    @Then("{int}개의 캠프사이트가 조회된다")
    public void campsitesAreRetrieved(int count) {
        CampsiteTestHelper.getLastResponse()
                .then()
                .statusCode(200)
                .body("$", Matchers.hasSize(count));
    }

    @Then("캠프사이트 목록 조회에 성공한다")
    public void campsiteListRetrievedSuccessfully() {
        CampsiteTestHelper.getLastResponse().then().statusCode(200);
    }

    @Then("캠프사이트 수정에 성공한다")
    public void campsiteUpdatedSuccessfully() {
        CampsiteTestHelper.getLastResponse().then().statusCode(200);
    }

    @Then("캠프사이트 등록에 실패한다")
    public void campsiteCreationFails() {
        Response response = CampsiteTestHelper.getLastResponse();
        ApiHelper.assertClientError(response);
    }
}
