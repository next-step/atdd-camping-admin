package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CampsiteStepDefs {
    private Response lastResponse;
    private Map<String, Object> campsiteData;

    @When("관리자가 {string} 캠프사이트를 최대 {int}명으로 등록한다")
    public void 관리자가캠프사이트를등록한다(String siteNumber, int maxPeople) {
        campsiteData = Map.of(
                "siteNumber", siteNumber,
                "description", "테스트용 캠프사이트",
                "maxPeople", maxPeople
        );
        
        lastResponse = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(campsiteData)
                .post("/admin/campsites");
    }

    @Then("캠프사이트 생성이 성공한다")
    public void 캠프사이트생성이성공한다() {
        lastResponse.then().statusCode(201);
    }

    @And("캠프사이트 정보가 올바르게 저장된다")
    public void 캠프사이트정보가올바르게저장된다() {
        lastResponse.then()
                .body("siteNumber", equalTo(campsiteData.get("siteNumber")))
                .body("description", equalTo(campsiteData.get("description")))
                .body("maxPeople", equalTo(campsiteData.get("maxPeople")))
                .body("id", notNullValue());
    }

    @And("새 캠프사이트는 예약 가능 상태다")
    public void 새캠프사이트는예약가능상태다() {
        Integer campsiteId = lastResponse.then().extract().path("id");
        String today = java.time.LocalDate.now().toString();
        
        given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .get("/admin/campsites/" + campsiteId + "/availability?date=" + today)
                .then().statusCode(200)
                .body("available", equalTo(true));
    }
}
