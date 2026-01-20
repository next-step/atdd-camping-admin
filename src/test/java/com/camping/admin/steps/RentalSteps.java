package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.steps.api.RentalApi;
import com.camping.admin.steps.context.TestContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
public class RentalSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestContext testContext;

    @Autowired
    private RentalApi rentalApi;

    private Long lastProductId;
    private ExtractableResponse<Response> lastResponse;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Given("재고가 {int}개인 {string} 대여 상품이 등록되어 있다")
    public void rentalProductIsRegistered(int stockQuantity, String productName) {
        Product product = new Product(productName, stockQuantity, BigDecimal.valueOf(10000), ProductType.RENTAL);
        Product savedProduct = productRepository.save(product);
        this.lastProductId = savedProduct.getId();
    }

    @When("관리자가 {string} 상품 {int}개를 대여 처리하면")
    public void adminCreatesRental(String productName, int quantity) {
        lastResponse = rentalApi.대여_생성_요청(testContext.getAdminToken(), lastProductId, quantity);
    }


    @Then("대여 기록이 정상적으로 생성된다")
    public void rentalRecordIsCreated() {
        assertThat(lastResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Then("{string} 상품의 재고는 {int}개가 된다")
    public void productStockShouldBe(String productName, int expectedStock) {
        Product product = productRepository.findById(lastProductId).orElseThrow();
        assertThat(product.getStockQuantity()).isEqualTo(expectedStock);
    }
}