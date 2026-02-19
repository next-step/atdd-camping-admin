package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class productSteps {

    @Autowired private TestContext context;
    @Autowired private ProductRepository productRepository;

    @Given("상품 {int}개가 등록되어 있다")
    public void 상품N개가등록되어있다(int count) {
        for (int i = 1; i <= count; i++) {
            productRepository.save(new Product("테스트 상품 " + i, 10, BigDecimal.valueOf(5000), ProductType.SALE));
        }
    }

    @Given("상품이 등록되어 있다")
    public void 상품이등록되어있다() {
        Product product = productRepository.save(
                new Product("코펠 세트", 15, BigDecimal.valueOf(20000), ProductType.RENTAL)
        );
        context.productId = product.getId();
    }

    @When("상품 목록을 조회한다")
    public void 상품목록을조회한다() {
        context.response = context.authRequest().get("/admin/products");
    }

    @When("판매 상품을 등록한다")
    public void 판매상품을등록한다() {
        context.response = context.authRequest()
                .body(Map.of("name", "모기향", "stockQuantity", 100, "price", 3000, "productType", "SALE"))
                .post("/admin/products");
        if (context.response.statusCode() == 201) {
            context.productId = context.response.jsonPath().getLong("id");
        }
    }

    @When("대여 상품을 등록한다")
    public void 대여상품을등록한다() {
        context.response = context.authRequest()
                .body(Map.of("name", "텐트", "stockQuantity", 10, "price", 50000, "productType", "RENTAL"))
                .post("/admin/products");
        if (context.response.statusCode() == 201) {
            context.productId = context.response.jsonPath().getLong("id");
        }
    }

    @When("상품 가격을 수정한다")
    public void 상품가격을수정한다() {
        context.response = context.authRequest()
                .body(Map.of("price", 35000))
                .put("/admin/products/" + context.productId);
    }

    @When("상품 재고를 수정한다")
    public void 상품재고를수정한다() {
        context.response = context.authRequest()
                .body(Map.of("stockQuantity", 50))
                .put("/admin/products/" + context.productId);
    }

    @Then("상품이 생성된다")
    public void 상품이생성된다() {
        context.response.then().statusCode(201);
    }

    @And("상품 {int}개가 반환된다")
    public void 상품N개가반환된다(int count) {
        context.response.then().body("$", hasSize(count));
    }

    @And("생성된 상품 정보가 반환된다")
    public void 생성된상품정보가반환된다() {
        context.response.then()
                .body("id", notNullValue())
                .body("name", notNullValue());
    }

    @And("수정된 상품 정보가 반환된다")
    public void 수정된상품정보가반환된다() {
        context.response.then().body("id", notNullValue());
    }
}
