package com.camping.admin.steps.when;

import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.RestAssuredConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

import java.util.List;
import java.util.Map;

public class SalesWhenSteps {

    private final SharedState state;

    public SalesWhenSteps(SharedState state) {
        this.state = state;
    }

    @When("해당 상품을 {int}개 판매하면")
    public void 해당_상품을_N개_판매하면(int quantity) {
        Map<String, Object> item = Map.of(
                "productId", state.getProductId(),
                "quantity", quantity
        );

        Map<String, Object> request = Map.of("items", List.of(item));

        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .body(request)
                .when()
                .post("/api/sales"));
    }

    @When("판매 목록을 조회하면")
    public void 판매_목록을_조회하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .when()
                .get("/api/sales"));
    }
}
