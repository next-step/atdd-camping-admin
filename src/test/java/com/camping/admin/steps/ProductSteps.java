package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.steps.api.ProductApi;
import com.camping.admin.steps.context.TestContext;
import com.camping.admin.steps.support.TestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
public class ProductSteps {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ProductApi productApi;

    @Given("재고가 {int}개인 {string} 대여 상품이 등록되어 있다")
    public void rentalProductIsRegistered(int stockQuantity, String productName) {
        Product product = testDataFactory.createProduct(productName, stockQuantity, BigDecimal.valueOf(10000), ProductType.RENTAL);
        testContext.setLastProductId(product.getId());
    }

    @Given("재고가 {int}개인 {string} 판매 상품이 등록되어 있다")
    public void saleProductIsRegistered(int stockQuantity, String productName) {
        Product product = testDataFactory.createProduct(productName, stockQuantity, BigDecimal.valueOf(10000), ProductType.SALE);
        testContext.setLastProductId(product.getId());
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
        Product product = productRepository.findById(testContext.getLastProductId()).orElseThrow();
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getStockQuantity()).isEqualTo(stockQuantity);
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(price));
        assertThat(product.getProductType().name()).isEqualTo(productType);
    }
}