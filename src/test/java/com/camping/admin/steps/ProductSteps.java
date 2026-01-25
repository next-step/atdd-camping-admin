package com.camping.admin.steps;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.steps.api.ProductApi;
import com.camping.admin.steps.context.TestContext;
import com.camping.admin.steps.support.ProductTypeMapper;
import com.camping.admin.steps.support.TestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
public class ProductSteps {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ProductApi productApi;

    @Given("재고가 {int}개인 {string} {string} 상품이 등록되어 있다")
    public void productIsRegistered(int stockQuantity, String productName, String inputProductType) {
        ProductType productType = ProductTypeMapper.from(inputProductType);
        Long productId = testDataFactory.createProduct(productName, stockQuantity, BigDecimal.valueOf(10000), productType);
        testContext.setLastProductId(productId);
    }

    @When("관리자가 이름 {string}, 재고 {int}개, 가격 {int}원, 타입 {string}인 상품을 등록하면")
    public void adminCreatesProduct(String name, int stockQuantity, int price, String productType) {
        testContext.setLastResponse(productApi.상품_생성_요청(testContext.getAdminToken(), name, stockQuantity, price, productType));
    }

    @Then("상품이 성공적으로 등록된다")
    public void productIsCreated() {
        assertThat(testContext.getLastResponse().statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long productId = testContext.getLastResponse().jsonPath().getLong("id");
        testContext.setLastProductId(productId);
    }

    @Then("상품 등록이 실패한다")
    public void productCreationShouldFail() {
        assertThat(testContext.getLastResponse().statusCode()).isGreaterThanOrEqualTo(400);
    }

    @And("등록된 상품의 이름은 {string}, 재고는 {int}개, 가격은 {int}원, 타입은 {string}이어야 한다")
    public void productDetailsShouldBe(String name, int stockQuantity, int price, String productType) {
        ExtractableResponse<Response> response = productApi.상품_목록_조회_요청(testContext.getAdminToken());
        List<Map<String, Object>> products = response.jsonPath().getList("");

        Map<String, Object> product = products.stream()
                .filter(p -> p.get("id").toString().equals(testContext.getLastProductId().toString()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + testContext.getLastProductId()));

        assertThat(product.get("name")).isEqualTo(name);
        assertThat(product.get("stockQuantity")).isEqualTo(stockQuantity);
        assertThat(new BigDecimal(product.get("price").toString())).isEqualByComparingTo(BigDecimal.valueOf(price));
        assertThat(product.get("productType")).isEqualTo(productType);
    }
}