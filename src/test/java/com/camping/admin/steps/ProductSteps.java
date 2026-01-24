package com.camping.admin.steps;

import com.camping.admin.fixture.TestConfig;
import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.RestAssuredConfig;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

import java.util.Map;

public class ProductSteps {

    private final SharedState state;

    public ProductSteps(SharedState state) {
        this.state = state;
    }

    // === Given ===

    @Given("수정할 상품이 존재한다")
    public void 수정할_상품이_존재한다() {
        state.setProductId(TestConfig.ProductIds.LANTERN);
    }

    // === When ===

    @When("상품을 생성하면")
    public void 상품을_생성하면() {
        Map<String, Object> request = Map.of(
                "name", "테스트 상품",
                "price", 10000,
                "stockQuantity", 10,
                "productType", "SALE"
        );

        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .body(request)
                .when()
                .post("/admin/products"));
    }

    @When("이름 없이 상품을 생성하면")
    public void 이름_없이_상품을_생성하면() {
        Map<String, Object> request = Map.of(
                "price", 5000,
                "stockQuantity", 5,
                "productType", "RENTAL"
        );

        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .body(request)
                .when()
                .post("/admin/products"));
    }

    @When("상품 목록을 조회하면")
    public void 상품_목록을_조회하면() {
        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .when()
                .get("/admin/products"));
    }

    @When("해당 상품 정보를 수정하면")
    public void 해당_상품_정보를_수정하면() {
        Map<String, Object> request = Map.of(
                "name", "수정된 상품명",
                "price", 15000
        );

        state.setResponse(RestAssured.given()
                .spec(RestAssuredConfig.createAuthenticatedSpec(AuthHelper.getAdminToken()))
                .body(request)
                .when()
                .put("/admin/products/" + state.getProductId()));
    }
}
