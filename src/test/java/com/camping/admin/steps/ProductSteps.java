package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

public class ProductSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private ProductRepository productRepository;

    @Given("이름이 {string}, 타입이 {string}, 가격이 {int}인 상품이 등록되어 있다")
    @Transactional
    public void 상품이_등록되어_있다(String name, String type, int price) {
        Product product = new Product(name, 10, new BigDecimal(price), ProductType.valueOf(type));
        Product savedProduct = productRepository.save(product);
        testContext.addProduct(name, savedProduct);
    }

    @When("관리자가 이름이 {string}, 타입이 {string}, 가격이 {int}인 상품을 등록하면")
    public void 관리자가_상품을_등록하면(String name, String type, int price) {
        var authToken = testContext.getAuthToken();

        Map<String, Object> productRequest = Map.of(
            "name", name,
            "productType", type,
            "price", price
        );

        var response = RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .contentType(ContentType.JSON)
            .body(productRequest)
            .when()
            .post("/admin/products")
            .then()
            .extract();

        testContext.setResponse(response);
    }

    @Then("상품 등록이 성공한다")
    public void 상품_등록이_성공한다() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
