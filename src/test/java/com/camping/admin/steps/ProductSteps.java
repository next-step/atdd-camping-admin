package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.ProductTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class ProductSteps {

    private final TestHelperContext helpers;
    private Response lastResponse;
    private int targetProductId;
    private String createdProductName;
    private String updatedProductName;
    private int updatedStockQuantity;
    private int updatedPrice;

    public ProductSteps(TestHelperContext helpers) {
        this.helpers = helpers;
    }

    private ProductTestHelper helper() {
        return helpers.product();
    }

    @When("관리자가 새 상품을 등록한다")
    public void 관리자가새상품을등록한다() {
        createdProductName = "테스트 상품-" + System.currentTimeMillis();
        lastResponse = helper().createProduct(createdProductName, 10, 5000, "SALE");
    }

    @Then("상품이 등록된다")
    public void 상품이등록된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(200, 201);

        Response getResponse = helper().getProducts();
        String foundName = getResponse.jsonPath()
            .getString("find { it.name == '" + createdProductName + "' }.name");
        assertThat(foundName).isEqualTo(createdProductName);
        System.out.println("[Then] 상품이 등록된다 - DB 반영 확인 완료");
    }

    @Given("등록된 상품이 있다")
    public void 등록된상품이있다() {
        Response response = helper().getProducts();
        response.then().statusCode(200);
        targetProductId = response.jsonPath().getInt("[0].id");
        System.out.println("[Given] 등록된 상품이 있다. ID: " + targetProductId);
    }

    @When("관리자가 상품 정보를 수정한다")
    public void 관리자가상품정보를수정한다() {
        updatedProductName = "수정된 상품-" + System.currentTimeMillis();
        updatedStockQuantity = 20;
        updatedPrice = 10000;
        lastResponse = helper().updateProduct(targetProductId, updatedProductName, updatedStockQuantity, updatedPrice);
    }

    @Then("상품 정보가 수정된다")
    public void 상품정보가수정된다() {
        lastResponse.then().statusCode(200);

        Response getResponse = helper().getProducts();
        String dbName = getResponse.jsonPath()
            .getString("find { it.id == " + targetProductId + " }.name");
        int dbStock = getResponse.jsonPath()
            .getInt("find { it.id == " + targetProductId + " }.stockQuantity");
        float dbPrice = getResponse.jsonPath()
            .getFloat("find { it.id == " + targetProductId + " }.price");

        assertThat(dbName).isEqualTo(updatedProductName);
        assertThat(dbStock).isEqualTo(updatedStockQuantity);
        assertThat(dbPrice).isEqualTo((float) updatedPrice);
        System.out.println("[Then] 상품 정보가 수정된다 - DB 반영 확인 완료");
    }

    @When("관리자가 재고 {int}인 상품을 등록한다")
    public void 관리자가재고인상품을등록한다(int stock) {
        lastResponse = helper().createProduct("재고 테스트 상품", stock, 5000, "SALE");
    }

    @Then("상품 등록이 거부된다")
    public void 상품등록이거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 409, 422);
        System.out.println("[Then] 상품 등록이 거부된다");
    }

    @When("관리자가 이름 없이 상품을 등록한다")
    public void 관리자가이름없이상품을등록한다() {
        lastResponse = helper().createProduct(null, 10, 5000, "SALE");
    }

    @When("관리자가 가격 없이 상품을 등록한다")
    public void 관리자가가격없이상품을등록한다() {
        lastResponse = helper().createProduct("가격 없는 상품", 10, 0, "SALE");
    }

    @When("관리자가 존재하지 않는 상품을 수정한다")
    public void 관리자가존재하지않는상품을수정한다() {
        lastResponse = helper().updateProduct(99999, "수정된 상품", 20, 10000);
    }

    @Then("상품 수정이 거부된다")
    public void 상품수정이거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 404, 409);
        System.out.println("[Then] 상품 수정이 거부된다");
    }
}