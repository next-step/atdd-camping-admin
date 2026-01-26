package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CampsiteSteps {

    private TestContext context = TestContext.getInstance();
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

    @When("관리자가 캠핑장을 등록한다")
    public void 관리자가_캠핑장을_등록한다() {
        context.setLastResponse(given()
                .spec(context.getAuthHelper().authorizedSpec())
                .body(Map.of(
                        "siteNumber", "A-99",
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
                .body("siteNumber", equalTo("A-99"));
    }

    @Then("캠핑장 수정이 완료된다")
    public void 캠핑장_수정이_완료된다() {
        context.getLastResponse().then()
                .statusCode(200)
                .body("description", equalTo("수정된 설명"));
    }
}
