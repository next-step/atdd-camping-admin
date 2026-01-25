package com.camping.admin.steps;

import static com.camping.admin.helper.ProductApiClient.*;

import io.cucumber.java.en.Given;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedSteps {

    private static final Logger log = LoggerFactory.getLogger(SharedSteps.class);

    private final ApiClientContext api;
    private final ScenarioContext scenario;

    public SharedSteps(ApiClientContext api, ScenarioContext scenario) {
        this.api = api;
        this.scenario = scenario;
    }

    @Given("재고가 {int}인 {word} 상품이 있다")
    public void 재고가n인타입상품이있다(int stock, String productType) {
        int targetProductId;
        if (stock == 0) {
            targetProductId = productType.equals("대여") ? OUT_OF_STOCK_RENTAL_ID : OUT_OF_STOCK_SALE_ID;
        } else if (stock == 5 && productType.equals("판매")) {
            targetProductId = LIMITED_STOCK_SALE_ID;
        } else {
            String type = productType.equals("대여") ? "RENTAL" : "SALE";
            targetProductId = api.product().findProductByTypeAndStock(type, stock);
        }

        scenario.setTargetProductId(targetProductId);
        scenario.setOriginalStock(stock);
        log.info("[Given] 재고가 {}인 {} 상품이 있다. ID: {}", stock, productType, targetProductId);
    }
}
