package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductStepDefs {
    private Response lastResponse;

    @When("관리자가 상품 목록을 조회한다")
    public void 관리자가상품목록을조회한다() {
        lastResponse = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .get("/admin/products");
    }

    @Then("상품 목록이 반환된다")
    public void 상품목록이반환된다() {
        lastResponse.then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @And("상품 정보에는 이름과 가격 정보가 포함된다")
    public void 상품정보에는이름과가격정보가포함된다() {
        lastResponse.then()
                .body("[0].name", notNullValue())
                .body("[0].price", notNullValue())
                .body("[0].productType", notNullValue());
    }
}
