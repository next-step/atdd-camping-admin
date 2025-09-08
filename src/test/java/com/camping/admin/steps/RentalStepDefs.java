package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RentalStepDefs {
    private Response lastResponse;

    @When("관리자가 대여 목록을 조회한다")
    public void 관리자가대여목록을조회한다() {
        lastResponse = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .get("/admin/rentals");
    }

    @Then("대여 목록이 반환된다")
    public void 대여목록이반환된다() {
        lastResponse.then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @And("대여 정보에는 상품과 수량 정보가 포함된다")
    public void 대여정보에는상품과수량정보가포함된다() {
        lastResponse.then()
                .body("[0].productId", notNullValue())
                .body("[0].quantity", notNullValue());
    }
}
