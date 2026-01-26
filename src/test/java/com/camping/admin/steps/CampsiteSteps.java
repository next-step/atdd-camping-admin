package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CampsiteSteps {

    @Autowired
    private TestContext testContext;

    @When("관리자가 사이트 번호 {string}, 설명 {string}, 수용 인원 {int}명의 캠프사이트를 등록 요청하면")
    public void 관리자가_캠프사이트를_등록하면(String siteNumber, String description, int maxPeople) {
        var authToken = testContext.getAuthToken();

        Map<String, Object> campsiteRequest = Map.of(
            "siteNumber", siteNumber,
            "description", description,
            "maxPeople", maxPeople
        );

        var response = RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .contentType(ContentType.JSON)
            .body(campsiteRequest)
            .when()
            .post("/admin/campsites")
            .then()
            .extract();

        testContext.setResponse(response);
    }

    @Then("캠프 사이트 등록이 성공한다")
    public void 캠프_사이트_등록이_성공한다() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @And("등록된 캠프사이트의 사이트 번호는 {string}이다")
    public void 등록된_캠프사이트의_사이트_번호는(String siteNumber) {
        var response = testContext.getResponse();
        String actual = response.jsonPath().getString("siteNumber");
        assertThat(actual).isEqualTo(siteNumber);
    }
}
