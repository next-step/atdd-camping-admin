package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CampsiteStepDefs {
    private Response lastResponse;

    @When("관리자가 캠프사이트 목록을 조회한다")
    public void 관리자가캠프사이트목록을조회한다() {
        lastResponse = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .get("/admin/campsites");
    }

    @Then("캠프사이트 목록이 반환된다")
    public void 캠프사이트목록이반환된다() {
        lastResponse.then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @And("캠프사이트 정보에는 사이트 번호와 최대 인원이 포함된다")
    public void 캠프사이트정보에는사이트번호와최대인원이포함된다() {
        lastResponse.then()
                .body("[0].siteNumber", notNullValue())
                .body("[0].maxPeople", notNullValue());
    }
}