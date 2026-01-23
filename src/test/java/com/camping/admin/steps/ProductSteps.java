package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.steps.context.TestContext;
import com.camping.admin.steps.support.TestDataFactory;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Given("재고가 {int}개인 {string} 상품인 {string} 판매 상품이 등록되어 있다")
    public void saleProductIsRegistered(int stockQuantity, String productType, String productName) {
        ProductType type = "세일".equals(productType) ? ProductType.SALE : ProductType.RENTAL;
        Product product = testDataFactory.createProduct(productName, stockQuantity, BigDecimal.valueOf(10000), type);
        testContext.setLastProductId(product.getId());
    }

    @Given("{string}은 세일 상품이다")
    public void productIsSaleItem(String productName) {
        Product product = productRepository.findById(testContext.getLastProductId()).orElseThrow();
        assertThat(product.getName()).isEqualTo(productName);
        assertThat(product.getProductType()).isEqualTo(ProductType.SALE);
    }
}