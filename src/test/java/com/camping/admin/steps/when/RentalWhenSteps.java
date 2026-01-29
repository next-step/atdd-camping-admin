package com.camping.admin.steps.when;

import com.camping.admin.fixture.RentalRequestBuilder;
import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.RestAssuredConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

public class RentalWhenSteps {

    private final SharedState state;

    public RentalWhenSteps(SharedState state) {
        this.state = state;
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

    @When("해당 대여 기록을 반납하면")
    public void 해당_대여_기록을_반납하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .when()
                .patch("/admin/rentals/" + state.getRentalRecordId() + "/return"));
    }
}
