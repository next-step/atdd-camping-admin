package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.ProductApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductSteps {

    private static final Logger log = LoggerFactory.getLogger(ProductSteps.class);

    private final ApiClientContext api;
    private final ScenarioContext scenario;

    private String updatedProductName;
    private int updatedStockQuantity;
    private int updatedPrice;

    public ProductSteps(ApiClientContext api, ScenarioContext scenario) {
        this.api = api;
        this.scenario = scenario;
    }

    private ProductApiClient helper() {
        return api.product();
    }

    @When("관리자가 새 상품을 등록한다")
    public void 관리자가새상품을등록한다() {
        String productName = "테스트 상품-" + System.currentTimeMillis();
        scenario.setCreatedProductName(productName);
        scenario.setLastResponse(helper().createProduct(productName, 10, 5000, "SALE"));
    }

    @Then("상품이 등록된다")
    public void 상품이등록된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(200, 201);

        Response getResponse = helper().getProducts();
        String createdProductName = scenario.getCreatedProductName();
        String foundName = getResponse.jsonPath()
            .getString("find { it.name == '" + createdProductName + "' }.name");
        assertThat(foundName).isEqualTo(createdProductName);
        log.info("[Then] 상품이 등록된다 - DB 반영 확인 완료");
    }

    @Given("등록된 상품이 있다")
    public void 등록된상품이있다() {
        Response response = helper().getProducts();
        response.then().statusCode(200);
        scenario.setTargetProductId(response.jsonPath().getInt("[0].id"));
        log.info("[Given] 등록된 상품이 있다. ID: {}", scenario.getTargetProductId());
    }

    @When("관리자가 상품 정보를 수정한다")
    public void 관리자가상품정보를수정한다() {
        updatedProductName = "수정된 상품-" + System.currentTimeMillis();
        updatedStockQuantity = 20;
        updatedPrice = 10000;
        scenario.setLastResponse(helper().updateProduct(
            scenario.getTargetProductId(), updatedProductName, updatedStockQuantity, updatedPrice));
    }

    @Then("상품 정보가 수정된다")
    public void 상품정보가수정된다() {
        scenario.getLastResponse().then().statusCode(200);

        Response getResponse = helper().getProducts();
        int targetProductId = scenario.getTargetProductId();
        String dbName = getResponse.jsonPath()
            .getString("find { it.id == " + targetProductId + " }.name");
        int dbStock = getResponse.jsonPath()
            .getInt("find { it.id == " + targetProductId + " }.stockQuantity");
        float dbPrice = getResponse.jsonPath()
            .getFloat("find { it.id == " + targetProductId + " }.price");

        assertThat(dbName).isEqualTo(updatedProductName);
        assertThat(dbStock).isEqualTo(updatedStockQuantity);
        assertThat(dbPrice).isEqualTo((float) updatedPrice);
        log.info("[Then] 상품 정보가 수정된다 - DB 반영 확인 완료");
    }

    @When("관리자가 재고 {int}인 상품을 등록한다")
    public void 관리자가재고인상품을등록한다(int stock) {
        scenario.setLastResponse(helper().createProduct("재고 테스트 상품", stock, 5000, "SALE"));
    }

    @Then("상품 등록이 거부된다")
    public void 상품등록이거부된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(400, 409, 422);
        log.info("[Then] 상품 등록이 거부된다");
    }

    @When("관리자가 이름 없이 상품을 등록한다")
    public void 관리자가이름없이상품을등록한다() {
        scenario.setLastResponse(helper().createProduct(null, 10, 5000, "SALE"));
    }

    @When("관리자가 가격 없이 상품을 등록한다")
    public void 관리자가가격없이상품을등록한다() {
        scenario.setLastResponse(helper().createProduct("가격 없는 상품", 10, 0, "SALE"));
    }

    @When("관리자가 존재하지 않는 상품을 수정한다")
    public void 관리자가존재하지않는상품을수정한다() {
        scenario.setLastResponse(helper().updateProduct(99999, "수정된 상품", 20, 10000));
    }

    @Then("상품 수정이 거부된다")
    public void 상품수정이거부된다() {
        int statusCode = scenario.getLastResponse().getStatusCode();
        assertThat(statusCode).isIn(400, 404, 409);
        log.info("[Then] 상품 수정이 거부된다");
    }
}
