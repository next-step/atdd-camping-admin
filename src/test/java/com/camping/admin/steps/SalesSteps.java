package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.ProductApiClient;
import com.camping.admin.helper.SalesApiClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalesSteps {

    private static final Logger log = LoggerFactory.getLogger(SalesSteps.class);

    private final ApiClientContext api;
    private final ScenarioContext scenario;

    public SalesSteps(ApiClientContext api, ScenarioContext scenario) {
        this.api = api;
        this.scenario = scenario;
    }

    private SalesApiClient salesHelper() {
        return api.sales();
    }

    private ProductApiClient productHelper() {
        return api.product();
    }

    @When("관리자가 판매 기록을 조회한다")
    public void 관리자가판매기록을조회한다() {
        scenario.setLastResponse(salesHelper().getSales());
    }

    @Then("판매 기록이 반환된다")
    public void 판매기록이반환된다() {
        scenario.getLastResponse().then().statusCode(200);
        log.info("[Then] 판매 기록이 반환된다");
    }

    @Given("판매 가능한 상품이 있다")
    public void 판매가능한상품이있다() {
        int targetProductId = productHelper().findProductByTypeAndStock("SALE", 1);
        scenario.setTargetProductId(targetProductId);
        scenario.setOriginalStock(productHelper().getProductStock(targetProductId));
        log.info("[Given] 판매 가능한 상품이 있다. ID: {}", targetProductId);
    }

    @When("관리자가 해당 상품을 판매한다")
    public void 관리자가해당상품을판매한다() {
        scenario.setLastResponse(salesHelper().createSale(scenario.getTargetProductId(), 1));
    }

    @Then("판매가 완료된다")
    public void 판매가완료된다() {
        scenario.getLastResponse().then().statusCode(200);

        int currentStock = productHelper().getProductStock(scenario.getTargetProductId());
        assertThat(currentStock).isLessThan(scenario.getOriginalStock());
        log.info("[Then] 판매가 완료된다 - DB 반영 확인 (재고: {} → {})", scenario.getOriginalStock(), currentStock);
    }

    @And("판매 상품 재고가 감소한다")
    public void 판매상품재고가감소한다() {
        int currentStock = productHelper().getProductStock(scenario.getTargetProductId());
        assertThat(currentStock).isLessThan(scenario.getOriginalStock());
        log.info("[And] 판매 상품 재고가 감소한다");
    }

    @When("관리자가 해당 상품을 {int}개 판매한다")
    public void 관리자가해당상품을n개판매한다(int quantity) {
        scenario.setLastResponse(salesHelper().createSale(scenario.getTargetProductId(), quantity));
    }

    @Then("판매가 거부된다")
    public void 판매가거부된다() {
        scenario.getLastResponse().then().statusCode(409);
        log.info("[Then] 판매가 거부된다 - 재고 부족");
    }
}
