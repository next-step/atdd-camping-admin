package com.camping.admin.steps;

import com.camping.admin.fixture.RentalRequestBuilder;
import com.camping.admin.fixture.TestConfig;
import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.RestAssuredConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

import static org.hamcrest.Matchers.equalTo;

public class RentalSteps {

    private final SharedState state;

    public RentalSteps(SharedState state) {
        this.state = state;
    }

    @Given("관리자가 로그인했다")
    public void 관리자가_로그인했다() {
        AuthHelper.getAdminToken();
    }

    @And("대여 가능한 상품이 존재한다")
    public void 대여_가능한_상품이_존재한다() {
        state.setProductId(TestConfig.ProductIds.LANTERN);
    }

    @When("해당 상품을 {int}개 대여하면")
    public void 해당_상품을_N개_대여하면(int quantity) {
        state.setResponse(RestAssured.given()
            .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
            .body(RentalRequestBuilder.builder()
                .productId(state.getProductId())
                .quantity(quantity)
                .build())
            .when()
            .post("/admin/rentals"));
    }

    @Then("대여가 성공한다")
    public void 대여가_성공한다() {
        state.getResponse().then()
            .body("isReturned", equalTo(false));
    }

    @And("응답 상태코드는 {int}이다")
    public void 응답_상태코드는_N이다(int statusCode) {
        state.getResponse().then()
            .statusCode(statusCode);
    }
}
