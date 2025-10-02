package com.camping.admin.steps;

import com.camping.admin.helpers.ApiHelper;
import com.camping.admin.helpers.ProductTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.math.BigDecimal;

public class ProductSteps {

    @Given("상품이 등록되어 있다")
    public void productExists() {
        String uniqueName = "텐트-" + System.currentTimeMillis();
        Long productId = ProductTestHelper.createAndGetProductId(uniqueName, 10, new BigDecimal("100000"), "RENTAL");
        ProductTestHelper.setProductId(productId);
    }

    @Given("상품이 {int}개 등록되어 있다")
    public void multipleProductsExist(int count) {
        long timestamp = System.currentTimeMillis();
        for (int i = 1; i <= count; i++) {
            ProductTestHelper.createProduct("상품" + i + "-" + timestamp, 10, new BigDecimal("10000"), "SALE");
        }
    }

    @When("관리자가 새로운 상품을 등록한다")
    public void createNewProduct() {
        String uniqueName = "신규상품-" + System.currentTimeMillis();
        Response response = ProductTestHelper.createProduct(uniqueName, 20, new BigDecimal("50000"), "SALE");
        ProductTestHelper.setResponse(response);
    }

    @When("관리자가 이름 없이 상품을 등록한다")
    public void createProductWithoutName() {
        Response response = ProductTestHelper.createProductWithoutName(10, new BigDecimal("10000"), "SALE");
        ProductTestHelper.setResponse(response);
    }

    @When("관리자가 상품 목록을 조회한다")
    public void getAllProducts() {
        Response response = ProductTestHelper.getAllProducts();
        ProductTestHelper.setResponse(response);
    }

    @When("관리자가 상품 정보를 수정한다")
    public void updateProduct() {
        Long productId = ProductTestHelper.getProductId();
        Response response = ProductTestHelper.updateProduct(productId, "수정된 상품", 30, new BigDecimal("150000"), "SALE");
        ProductTestHelper.setResponse(response);
    }

    @Then("상품 등록에 성공한다")
    public void productCreatedSuccessfully() {
        ProductTestHelper.getLastResponse().then().statusCode(201);
    }

    @Then("{int}개의 상품이 조회된다")
    public void productsAreRetrieved(int count) {
        ProductTestHelper.getLastResponse()
                .then()
                .statusCode(200)
                .body("$", Matchers.hasSize(count));
    }

    @Then("상품 목록 조회에 성공한다")
    public void productListRetrievedSuccessfully() {
        ProductTestHelper.getLastResponse().then().statusCode(200);
    }

    @Then("상품 수정에 성공한다")
    public void productUpdatedSuccessfully() {
        ProductTestHelper.getLastResponse().then().statusCode(200);
    }

    @Then("상품 등록에 실패한다")
    public void productCreationFails() {
        Response response = ProductTestHelper.getLastResponse();
        ApiHelper.assertClientError(response);
    }
}
