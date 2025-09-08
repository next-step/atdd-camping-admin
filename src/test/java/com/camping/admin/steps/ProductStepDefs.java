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

    @When("관리자가 {string} 상품을 {string} 타입으로 등록한다")
    public void 관리자가상품을등록한다(String productName, String productType) {
        int stockQuantity = "SALE".equals(productType) ? 100 : 50;
        String price = "SALE".equals(productType) ? "15000.00" : "25000.00";
        
        productData = Map.of(
                "name", productName,
                "stockQuantity", stockQuantity,
                "price", new BigDecimal(price),
                "productType", productType
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
