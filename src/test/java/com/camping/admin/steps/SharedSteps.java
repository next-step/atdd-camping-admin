package com.camping.admin.steps;

import static com.camping.admin.helper.ProductTestHelper.*;

import io.cucumber.java.en.Given;

public class SharedSteps {

    private final TestHelperContext helpers;

    public SharedSteps(TestHelperContext helpers) {
        this.helpers = helpers;
    }

    @Given("재고가 {int}인 {word} 상품이 있다")
    public void 재고가n인타입상품이있다(int stock, String productType) {
        int targetProductId;
        if (stock == 0) {
            targetProductId = productType.equals("대여") ? OUT_OF_STOCK_RENTAL_ID : OUT_OF_STOCK_SALE_ID;
        } else if (stock == 5 && productType.equals("판매")) {
            // 재고 검증 테스트용: 정확히 5개 재고의 상품 사용
            targetProductId = LIMITED_STOCK_SALE_ID;
        } else {
            String type = productType.equals("대여") ? "RENTAL" : "SALE";
            targetProductId = helpers.product().findProductByTypeAndStock(type, stock);
        }

        helpers.product().setTargetProductId(targetProductId);
        helpers.product().setOriginalStock(stock);
        System.out.println("[Given] 재고가 " + stock + "인 " + productType + " 상품이 있다. ID: " + targetProductId);
    }
}