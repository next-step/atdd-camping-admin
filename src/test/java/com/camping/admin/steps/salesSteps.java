package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.SalesRecordRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class salesSteps {

    @Autowired private TestContext context;
    @Autowired private ProductRepository productRepository;
    @Autowired private SalesRecordRepository salesRecordRepository;

    @Given("판매 상품이 등록되어 있다")
    public void 판매상품이등록되어있다() {
        Product product = productRepository.save(
                new Product("장작팩", 50, BigDecimal.valueOf(10000), ProductType.SALE)
        );
        context.productId = product.getId();
        context.productStockBefore = product.getStockQuantity();
    }

    @Given("추가 판매 상품이 등록되어 있다")
    public void 추가판매상품이등록되어있다() {
        Product product = productRepository.save(
                new Product("생수(2L)", 100, BigDecimal.valueOf(2000), ProductType.SALE)
        );
        context.productId2 = product.getId();
        context.productStockBefore2 = product.getStockQuantity();
    }

    @Given("판매 기록이 존재한다")
    public void 판매기록이존재한다() {
        Product product = productRepository.findById(context.productId).orElseThrow();
        salesRecordRepository.save(new SalesRecord(product, 3, BigDecimal.valueOf(30000)));
    }

    @When("상품을 판매한다")
    public void 상품을판매한다() {
        context.response = context.authRequest()
                .body(Map.of("items", List.of(Map.of("productId", context.productId, "quantity", 3))))
                .post("/api/sales");
    }

    @When("여러 상품을 한번에 판매한다")
    public void 여러상품을한번에판매한다() {
        context.response = context.authRequest()
                .body(Map.of("items", List.of(
                        Map.of("productId", context.productId, "quantity", 2),
                        Map.of("productId", context.productId2, "quantity", 5)
                )))
                .post("/api/sales");
    }

    @When("판매 내역을 조회한다")
    public void 판매내역을조회한다() {
        context.response = context.authRequest().get("/api/sales");
    }

    @Then("판매가 처리된다")
    public void 판매가처리된다() {
        context.response.then().statusCode(200);
    }

    @And("판매 내역이 반환된다")
    public void 판매내역이반환된다() {
        context.response.then().body("$", not(empty()));
    }

    // DB 직접 조회이므로 AssertJ 사용
    @And("각 상품의 재고가 감소한다")
    public void 각상품의재고가감소한다() {
        int stock1 = productRepository.findById(context.productId).orElseThrow().getStockQuantity();
        int stock2 = productRepository.findById(context.productId2).orElseThrow().getStockQuantity();
        assertThat(stock1).isLessThan(context.productStockBefore);
        assertThat(stock2).isLessThan(context.productStockBefore2);
    }
}
