package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CampsiteSteps {

    private TestContext context = TestContext.getInstance();
    private String lastCreatedSiteNumber;
    // Given

    @Given("등록된 캠핑장 {int}이 있다")
    public void 등록된_캠핑장이_있다(int id) {
        given()
                .spec(context.getAuthHelper().authorizedSpec())
                .when()
                .get("/admin/campsites")
                .then()
                .statusCode(200)
                .body("find { it.id == " + id + " }", notNullValue());
    }

    // When

    @When("관리자가 캠핑장 목록을 조회한다")
    public void 관리자가_캠핑장_목록을_조회한다() {
        context.setLastResponse(given()
                .spec(context.getAuthHelper().authorizedSpec())
                .when()
                .get("/admin/campsites")
                .then()
                .extract().response());
    }

    @Given("삭제할 캠핑장이 등록되어 있다")
    public void 삭제할_캠핑장이_등록되어_있다() {
        String uniqueSiteNumber = "DEL-" + UUID.randomUUID().toString().substring(0, 8);
        context.setLastResponse(given()
                .spec(context.getAuthHelper().authorizedSpec())
                .body(Map.of(
                        "siteNumber", uniqueSiteNumber,
                        "description", "삭제 테스트용",
                        "maxPeople", 4
                ))
                .when()
                .post("/admin/campsites")
                .then()
                .extract().response());
    }

    @When("관리자가 캠핑장을 등록한다")
    public void 관리자가_캠핑장을_등록한다() {
        lastCreatedSiteNumber = "A-" + UUID.randomUUID().toString().substring(0, 8);
        context.setLastResponse(given()
                .spec(context.getAuthHelper().authorizedSpec())
                .body(Map.of(
                        "siteNumber", lastCreatedSiteNumber,
                        "description", "테스트 캠핑장",
                        "maxPeople", 6
                ))
                .when()
                .post("/admin/campsites")
                .then()
                .extract().response());
    }

    @When("관리자가 캠핑장 {int}을 수정한다")
    public void 관리자가_캠핑장을_수정한다(int id) {
        context.setLastResponse(given()
                .spec(context.getAuthHelper().authorizedSpec())
                .body(Map.of("description", "수정된 설명"))
                .when()
                .put("/admin/campsites/" + id)
                .then()
                .extract().response());
    }

    @When("관리자가 존재하지 않는 캠핑장 {int}을 수정한다")
    public void 관리자가_존재하지_않는_캠핑장을_수정한다(int id) {
        context.setLastResponse(given()
                .spec(context.getAuthHelper().authorizedSpec())
                .body(Map.of("description", "수정 시도"))
                .when()
                .put("/admin/campsites/" + id)
                .then()
                .extract().response());
    }

    @When("관리자가 캠핑장 {int}을 삭제한다")
    public void 관리자가_캠핑장을_삭제한다(int id) {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .when()
                        .delete("/admin/campsites/" + id)
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 예약이 있는 캠핑장 {int}을 삭제한다")
    public void 관리자가_예약이_있는_캠핑장을_삭제한다(int id) {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .when()
                        .delete("/admin/campsites/" + id)
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 등록한 캠핑장을 삭제한다")
    public void 관리자가_등록한_캠핑장을_삭제한다() {
        Long createdId = context.getLastResponse().jsonPath().getLong("id");
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .when()
                        .delete("/admin/campsites/" + createdId)
                        .then()
                        .extract().response()
        );
    }

    // Then

    @Then("캠핑장 목록이 조회된다")
    public void 캠핑장_목록이_조회된다() {
        context.getLastResponse().then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Then("캠핑장 등록이 완료된다")
    public void 캠핑장_등록이_완료된다() {
        context.getLastResponse().then()
                .statusCode(201)
                .body("siteNumber", equalTo(lastCreatedSiteNumber));
    }

    @Then("캠핑장 수정이 완료된다")
    public void 캠핑장_수정이_완료된다() {
        context.getLastResponse().then()
                .statusCode(200)
                .body("description", equalTo("수정된 설명"));
    }

    @Then("삭제가 완료된다")
    public void 삭제가_완료된다() {
        context.getLastResponse().then()
                .statusCode(204);
    }
}
