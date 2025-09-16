package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.Map;

import static com.camping.admin.support.Role.관리자;
import static org.assertj.core.api.Assertions.assertThat;

import static com.camping.admin.hooks.TokenHook.context;

public class CampsiteSteps {
    @When("관리자가 캠프사이트 목록을 조회 한다.")
    public void 관리자가캠프사이트목록을조회한다() {
        var response = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + context.getToken(관리자))
            .when().get("/admin/campsites")
            .then().log().all()
            .extract().response();

        context.setResponse(response);
    }

    @Then("캠프사이트 목록 조회가 성공 된다.")
    public void 캠프사이트목록조회가성공된다() {
        var response = context.getResponse();
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @And("캠프사이트 목록이 {int}개 조회 된다.")
    public void 캠프사이트목록이개조회된다(int expectedCount) {
        var response = context.getResponse();
        int actualCount = response.jsonPath().getList("$").size();
        assertThat(actualCount).isEqualTo(expectedCount);
    }

    @Given("캠프사이트 데이터를 모두 삭제 한다.")
    public void 캠프사이트데이터를모두삭제한다() {
        var response = RestAssured.given()
                .contentType(ContentType.JSON)
                .when().delete("/test-api/data/cleanup")
                .then().log().all()
                .extract().response();
        context.setResponse(response);
    }

    @When("관리자가 {string} 캠프사이트를 생성 한다.")
    public void createCampsite(String siteNumber) {
        Map<String, Object> request = Map.of(
            "siteNumber", siteNumber,
            "description", "새로운 캠프사이트",
            "maxPeople", 4
        );

        var response = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + context.getToken(관리자))
            .body(request)
            .when().post("/admin/campsites")
            .then().log().all()
            .extract().response();

        context.setResponse(response);
        // 생성된 캠프사이트 ID를 저장
        if (response.statusCode() == 201) {
            Long campsiteId = response.jsonPath().getLong("id");
            context.setData("campsiteId", campsiteId);
        }
    }

    @Then("캠프사이트 생성이 성공 된다.")
    public void successCreateCampsite() {
        var response = context.getResponse();
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Then("{string} 캠프사이트가 생성 된다.")
    public void checkCampsiteCreated(String siteNumber) {
        var response = context.getResponse();
        assertThat(response.jsonPath().getString("siteNumber")).isEqualTo(siteNumber);
        assertThat(response.jsonPath().getString("description")).isEqualTo("새로운 캠프사이트");
    }

    @When("관리자가 캠프사이트를 {string} 로 수정 한다.")
    public void updateCampsite(String siteNumber) {
        Long campsiteId = (Long) context.getData("campsiteId");
        String url = "/admin/campsites/" + campsiteId;

        Map<String, Object> request = Map.of(
            "siteNumber", siteNumber,
            "description", "수정된 캠프사이트",
            "maxPeople", 6
        );

        var response = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + context.getToken(관리자))
            .body(request)
            .when().put(url)
            .then().log().all()
            .extract().response();

        context.setResponse(response);
    }

    @Then("캠프사이트 수정이 성공 된다.")
    public void successUpdateCampsite() {
        var response = context.getResponse();
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Then("캠프사이트가 {string} 로 수정 된다.")
    public void checkCampsiteUpdated(String siteNumber) {
        var response = context.getResponse();
        assertThat(response.jsonPath().getString("siteNumber")).isEqualTo(siteNumber);
        assertThat(response.jsonPath().getString("description")).isEqualTo("수정된 캠프사이트");
    }

    @When("관리자가 존재하지 않는 캠프사이트를 수정 한다.")
    public void updateNonExistentCampsite() {
        String url = "/admin/campsites/999999"; // 존재하지 않는 ID

        Map<String, Object> request = Map.of(
            "siteNumber", "A-99",
            "description", "수정된 캠프사이트",
            "maxPeople", 6
        );

        var response = RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + context.getToken(관리자))
            .body(request)
            .when().put(url)
            .then().log().all()
            .extract().response();

        context.setResponse(response);
    }

    @Then("존재하지 않는 캠프사이트 수정이 실패 된다.")
    public void failUpdateNonExistentCampsite() {
        var response = context.getResponse();
        assertThat(response.statusCode()).isEqualTo(400);
    }
}
