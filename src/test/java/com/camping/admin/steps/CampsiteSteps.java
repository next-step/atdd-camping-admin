package com.camping.admin.steps;

import com.camping.admin.fixture.TestConfig;
import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.RestAssuredConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

import java.util.Map;

public class CampsiteSteps {

    private final SharedState state;

    public CampsiteSteps(SharedState state) {
        this.state = state;
    }

    // === Given ===

    @Given("수정할 캠프사이트가 존재한다")
    public void 수정할_캠프사이트가_존재한다() {
        state.setCampsiteId(TestConfig.CampsiteIds.EXISTING);
    }

    @Given("존재하지 않는 캠프사이트 ID를 사용한다")
    public void 존재하지_않는_캠프사이트_ID를_사용한다() {
        state.setCampsiteId(TestConfig.CampsiteIds.NOT_EXIST);
    }

    // === When ===

    @When("캠프사이트 목록을 조회하면")
    public void 캠프사이트_목록을_조회하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .when()
                .get("/admin/campsites"));
    }

    @When("캠프사이트를 생성하면")
    public void 캠프사이트를_생성하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .body(Map.of(
                        "siteNumber", "B-01",
                        "description", "테스트 사이트",
                        "maxPeople", 4
                ))
                .when()
                .post("/admin/campsites"));
    }

    @When("해당 캠프사이트 정보를 수정하면")
    public void 해당_캠프사이트_정보를_수정하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .body(Map.of(
                        "description", "수정된 설명",
                        "maxPeople", 8
                ))
                .when()
                .put("/admin/campsites/" + state.getCampsiteId()));
    }
}
