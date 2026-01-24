package com.camping.admin.steps;

import com.camping.admin.fixture.RentalRequestBuilder;
import com.camping.admin.fixture.TestConfig;
import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.RestAssuredConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

public class RentalSteps {

    private final SharedState state;

    public RentalSteps(SharedState state) {
        this.state = state;
    }

    // === Given ===

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

    // === When ===

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

    @When("해당 대여 기록을 반납하면")
    public void 해당_대여_기록을_반납하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .when()
                .patch("/admin/rentals/" + state.getRentalRecordId() + "/return"));
    }
}
