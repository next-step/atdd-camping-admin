package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ProductStepDefs {
    private Response lastResponse;
    private Map<String, Object> productData;

    @When("관리자가 판매용 상품을 등록한다")
    public void 관리자가판매용상품을등록한다() {
        productData = Map.of(
                "name", "테스트 판매 상품",
                "stockQuantity", 100,
                "price", new BigDecimal("15000.00"),
                "productType", "SALE"
        );
        
        lastResponse = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(productData)
                .post("/admin/products");
    }

    @When("관리자가 대여용 상품을 등록한다")
    public void 관리자가대여용상품을등록한다() {
        productData = Map.of(
                "name", "테스트 대여 상품",
                "stockQuantity", 50,
                "price", new BigDecimal("25000.00"),
                "productType", "RENTAL"
        );
        
        lastResponse = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(productData)
                .post("/admin/products");
    }

    @Then("상품 생성이 성공한다")
    public void 상품생성이성공한다() {
        lastResponse.then().statusCode(201);
    }

    @And("상품 정보가 올바르게 저장된다")
    public void 상품정보가올바르게저장된다() {
        lastResponse.then()
                .body("name", equalTo(productData.get("name")))
                .body("stockQuantity", equalTo(productData.get("stockQuantity")))
                .body("price", equalTo(((BigDecimal) productData.get("price")).floatValue()))
                .body("productType", equalTo(productData.get("productType")))
                .body("id", notNullValue());
    }
}
