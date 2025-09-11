package com.camping.admin.steps;

import static com.camping.admin.steps.hooks.Hooks.authenticatedRequest;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class CampsiteSteps {

    private Response response;
    private String generatedSiteNumber;

    @When("고유한 사이트 번호와 설명 {string}, 최대 인원 {int}명으로 캠프사이트 생성을 요청한다")
    public void requestCampsiteCreation(String description, int maxPeople) {
        Map<String, Object> campsiteDetails = new HashMap<>();
        generatedSiteNumber = "A-" + System.currentTimeMillis();
        campsiteDetails.put("siteNumber", generatedSiteNumber);
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

    @And("생성된 캠프사이트의 사이트 번호는 생성 시 사용한 번호이다")
    public void theCampsiteSiteNumberIsTheGeneratedOne() {
        response.then().body("siteNumber", equalTo(generatedSiteNumber));
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
