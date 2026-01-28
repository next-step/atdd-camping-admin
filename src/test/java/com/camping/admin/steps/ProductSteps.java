package com.camping.admin.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 상품 생성 기능의 인수 테스트 Step 정의
 */
public class ProductSteps extends CucumberSpringConfiguration {

    @Autowired
    private ProductTestHelper helper;

    // ==================== When ====================

    @When("관리자가 다음 정보로 상품을 등록한다")
    public void 관리자가_다음_정보로_상품을_등록한다(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> row = rows.get(0);

        String name = row.get("상품명");
        int stockQuantity = Integer.parseInt(row.get("재고"));
        BigDecimal price = new BigDecimal(row.get("가격"));
        String productType = 상품유형을_변환한다(row.get("유형"));

        helper.상품_정보로_등록을_요청한다(name, stockQuantity, price, productType);
    }

    @When("관리자가 상품명 없이 상품 등록을 요청한다")
    public void 관리자가_상품명_없이_상품_등록을_요청한다() {
        helper.상품명_없이_상품_등록을_요청한다();
    }

    @When("관리자가 유효하지 않은 유형으로 상품 등록을 요청한다")
    public void 관리자가_유효하지_않은_유형으로_상품_등록을_요청한다() {
        helper.유효하지_않은_유형으로_상품_등록을_요청한다();
    }

    @When("관리자가 가격을 {int}원으로 상품 등록을 요청한다")
    public void 관리자가_가격을_N원으로_상품_등록을_요청한다(int price) {
        helper.가격을_음수로_상품_등록을_요청한다(price);
    }

    @When("관리자가 재고를 {int}개로 상품 등록을 요청한다")
    public void 관리자가_재고를_N개로_상품_등록을_요청한다(int stockQuantity) {
        helper.재고를_음수로_상품_등록을_요청한다(stockQuantity);
    }

    @When("관리자가 상품명을 빈 값으로 상품 등록을 요청한다")
    public void 관리자가_상품명을_빈_값으로_상품_등록을_요청한다() {
        helper.상품명을_빈_값으로_상품_등록을_요청한다();
    }

    // ==================== Then ====================

    @Then("상품이 등록된다")
    public void 상품이_등록된다() {
        helper.상품이_등록되었는지_검증한다();
    }

    @Then("상품은 등록되지 않는다")
    public void 상품은_등록되지_않는다() {
        helper.상품이_등록되지_않았는지_검증한다();
    }

    @Then("상품 목록에 {string}가 포함된다")
    public void 상품_목록에_포함된다(String productName) {
        helper.상품_목록에_포함되는지_검증한다(productName);
    }

    // ==================== Private ====================

    private String 상품유형을_변환한다(String displayName) {
        return switch (displayName) {
            case "판매용" -> "SALE";
            case "대여용" -> "RENTAL";
            default -> displayName;
        };
    }
}