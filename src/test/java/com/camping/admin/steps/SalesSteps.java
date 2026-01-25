package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.ProductTestHelper;
import com.camping.admin.helper.SalesTestHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class SalesSteps {

    private final TestHelperContext helpers;
    private Response lastResponse;
    private int targetProductId;
    private int originalStock;

    public SalesSteps(TestHelperContext helpers) {
        this.helpers = helpers;
    }

    private SalesTestHelper salesHelper() {
        return helpers.sales();
    }

    private ProductTestHelper productHelper() {
        return helpers.product();
    }

    @When("관리자가 판매 기록을 조회한다")
    public void 관리자가판매기록을조회한다() {
        lastResponse = salesHelper().getSales();
    }

    @Then("판매 기록이 반환된다")
    public void 판매기록이반환된다() {
        lastResponse.then().statusCode(200);
        System.out.println("[Then] 판매 기록이 반환된다");
    }

    @Given("판매 가능한 상품이 있다")
    public void 판매가능한상품이있다() {
        targetProductId = productHelper().findProductByTypeAndStock("SALE", 1);
        originalStock = productHelper().getProductStock(targetProductId);
        System.out.println("[Given] 판매 가능한 상품이 있다. ID: " + targetProductId);
    }

    @When("관리자가 해당 상품을 판매한다")
    public void 관리자가해당상품을판매한다() {
        lastResponse = salesHelper().createSale(targetProductId, 1);
    }

    @Then("판매가 완료된다")
    public void 판매가완료된다() {
        lastResponse.then().statusCode(200);

        int currentStock = productHelper().getProductStock(targetProductId);
        assertThat(currentStock).isLessThan(originalStock);
        System.out.println("[Then] 판매가 완료된다 - DB 반영 확인 (재고: " + originalStock + " → " + currentStock + ")");
    }

    @And("판매 상품 재고가 감소한다")
    public void 판매상품재고가감소한다() {
        int currentStock = productHelper().getProductStock(targetProductId);
        assertThat(currentStock).isLessThan(originalStock);
        System.out.println("[And] 판매 상품 재고가 감소한다");
    }

    @When("관리자가 해당 상품을 {int}개 판매한다")
    public void 관리자가해당상품을n개판매한다(int quantity) {
        // SharedSteps에서 설정된 경우 productHelper에서 가져옴
        int productId = targetProductId > 0 ? targetProductId : productHelper().getTargetProductId();
        lastResponse = salesHelper().createSale(productId, quantity);
    }

    @Then("판매가 거부된다")
    public void 판매가거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 409);
        System.out.println("[Then] 판매가 거부된다");
    }
}