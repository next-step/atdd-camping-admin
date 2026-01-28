package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RentalSteps {

    private TestContext context = TestContext.getInstance();

    // Given
    @Given("반납할 대여가 등록되어 있다")
    public void 반납할_대여가_등록되어_있다() {
        // 새 대여를 등록하고 응답을 저장
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .body(Map.of(
                                "productId", 4,  // 의자 (RENTAL 타입)
                                "quantity", 1,
                                "reservationId", 1
                        ))
                        .when()
                        .post("/admin/rentals")
                        .then()
                        .extract().response()
        );
    }

    // When
    @When("관리자가 대여 목록을 조회한다")
    public void 관리자가_대여_목록을_조회한다() {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .when()
                        .get("/admin/rentals")
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 장비 대여를 등록한다")
    public void 관리자가_장비_대여를_등록한다() {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .body(Map.of(
                                "productId", 3,
                                "quantity", 1,
                                "reservationId", 1
                        ))
                        .when()
                        .post("/admin/rentals")
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 장비 {int}을 반납 처리한다")
    public void 관리자가_장비를_반납_처리한다(int id) {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .when()
                        .patch("/admin/rentals/" + id + "/return")
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 등록한 대여를 반납 처리한다")
    public void 관리자가_등록한_대여를_반납_처리한다() {
        Long createdId = context.getLastResponse().jsonPath().getLong("id");
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .when()
                        .patch("/admin/rentals/" + createdId + "/return")
                        .then()
                        .extract().response()
        );
    }

    @When("관리자가 존재하지 않는 대여 {int}을 반납 처리한다")
    public void 관리자가_존재하지_않는_대여를_반납_처리한다(int id) {
        context.setLastResponse(
                given()
                        .spec(context.getAuthHelper().authorizedSpec())
                        .when()
                        .patch("/admin/rentals/" + id + "/return")
                        .then()
                        .extract().response()
        );
    }

    // Then

    @Then("대여 목록이 조회된다")
    public void 대여_목록이_조회된다() {
        context.getLastResponse().then()
                .statusCode(200);
    }

    @Then("대여 등록이 완료된다")
    public void 대여_등록이_완료된다() {
        context.getLastResponse().then()
                .statusCode(201);
    }

    @Then("반납 처리가 완료된다")
    public void 반납_처리가_완료된다() {
        context.getLastResponse().then()
                .statusCode(200)
                .body("isReturned", equalTo(true));
    }


}
