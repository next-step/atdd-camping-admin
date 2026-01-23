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

    // === 공통 ===

    @Given("관리자가 로그인했다")
    public void 관리자가_로그인했다() {
        AuthHelper.getAdminToken();
    }

    @And("응답 상태코드는 {int}이다")
    public void 응답_상태코드는_N이다(int statusCode) {
        state.getResponse().then()
            .statusCode(statusCode);
    }

    // === 대여 생성 Given ===

    @Given("대여 가능한 상품이 존재한다")
    public void 대여_가능한_상품이_존재한다() {
        state.setProductId(TestConfig.ProductIds.LANTERN);
    }

    @Given("판매 전용 상품이 존재한다")
    public void 판매_전용_상품이_존재한다() {
        state.setProductId(TestConfig.ProductIds.FIREWOOD);
    }

    @Given("존재하지 않는 상품 ID를 사용한다")
    public void 존재하지_않는_상품_ID를_사용한다() {
        state.setProductId(TestConfig.ProductIds.NOT_EXIST);
    }

    // === 대여 생성 When/Then ===

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
        System.out.println(">>> Response Body: " + state.getResponse().getBody().asString());
        state.getResponse().then()
            .body("isReturned", equalTo(false));
    }


    // === 반납 Given ===

    @Given("반납되지 않은 대여 기록이 존재한다")
    public void 반납되지_않은_대여_기록이_존재한다() {
        state.setRentalRecordId(TestConfig.RentalRecordIds.NOT_RETURNED);
    }

    @Given("이미 반납된 대여 기록이 존재한다")
    public void 이미_반납된_대여_기록이_존재한다() {
        state.setRentalRecordId(TestConfig.RentalRecordIds.ALREADY_RETURNED);
    }

    @Given("존재하지 않는 대여 기록 ID를 사용한다")
    public void 존재하지_않는_대여_기록_ID를_사용한다() {
        state.setRentalRecordId(TestConfig.RentalRecordIds.NOT_EXIST);
    }

    // === 반납 When/Then ===

    @When("해당 대여 기록을 반납하면")
    public void 해당_대여_기록을_반납하면() {
        state.setResponse(RestAssured.given()
            .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
            .when()
            .patch("/admin/rentals/" + state.getRentalRecordId() + "/return"));
    }

    @Then("반납이 성공한다")
    public void 반납이_성공한다() {
        System.out.println(">>> Response Body: " + state.getResponse().getBody().asString());
        state.getResponse().then()
            .body("isReturned", equalTo(true));
    }
}
