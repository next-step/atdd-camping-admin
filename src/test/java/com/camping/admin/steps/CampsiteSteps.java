package com.camping.admin.steps;

import static com.camping.admin.steps.hooks.Hooks.authenticatedRequest;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class CampsiteSteps {

    private Response response;

    @Given("{string} 사이트 번호로 캠프사이트가 이미 존재한다")
    public void campsiteWithSiteNumberAlreadyExists(String siteNumber) {
        Map<String, Object> existingCampsite = new HashMap<>();
        existingCampsite.put("siteNumber", siteNumber);
        existingCampsite.put("description", "기존 캠프사이트");
        existingCampsite.put("maxPeople", 4);

        authenticatedRequest.body(existingCampsite)
                .when()
                .post("/admin/campsites")
                .then()
                .statusCode(201);
    }

    @When("사이트 번호 {string}, 설명 {string}, 최대 인원 {int}명으로 캠프사이트 생성을 요청한다")
    public void requestCampsiteCreationWithSiteNumber(String siteNumber, String description, int maxPeople) {
        Map<String, Object> campsiteDetails = new HashMap<>();
        campsiteDetails.put("siteNumber", siteNumber);
        campsiteDetails.put("description", description);
        campsiteDetails.put("maxPeople", maxPeople);

        response = authenticatedRequest.body(campsiteDetails)
                .when()
                .post("/admin/campsites");
    }

    @Then("캠프사이트가 성공적으로 생성된다")
    public void theCampsiteIsSuccessfullyCreated() {
        response.then().statusCode(201);
    }

    @Then("캠프사이트 생성에 실패하고 오류 메시지를 받는다")
    public void campsiteCreationFailsWithErrorMessage() {
        response.then().assertThat().statusCode(409);
    }

    @And("생성된 캠프사이트의 사이트 번호는 {string}이다")
    public void theCampsiteSiteNumberIs(String siteNumber) {
        response.then().body("siteNumber", equalTo(siteNumber));
    }

    @And("생성된 캠프사이트의 설명은 {string}이다")
    public void theCampsiteDescriptionIs(String description) {
        response.then().body("description", equalTo(description));
    }

    @And("생성된 캠프사이트의 최대 인원은 {int}명이다")
    public void theCampsiteMaxPeopleIs(int maxPeople) {
        response.then().body("maxPeople", equalTo(maxPeople));
    }

    @And("생성된 캠프사이트는 고유한 ID를 가진다")
    public void theCampsiteHasAUniqueId() {
        response.then().body("id", notNullValue());
        response.then().body("id", isA(Number.class));
    }
}
