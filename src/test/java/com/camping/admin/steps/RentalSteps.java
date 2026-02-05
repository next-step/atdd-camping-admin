package com.camping.admin.steps;

import com.camping.admin.CucumberSpringConfiguration;
import com.camping.admin.helper.RentalTestHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 대여 생성 기능의 인수 테스트 Step 정의
 */
public class RentalSteps extends CucumberSpringConfiguration {

    @Autowired
    private RentalTestHelper helper;

    // ==================== Given ====================

//    @Given("관리자가 로그인되어 있다")
//    public void 관리자가_로그인되어_있다() {
//        // @Before 훅에서 이미 처리됨 - Background 문서화 목적
//    }

    @Given("대여용 상품이 재고 {int}개로 등록되어 있다")
    public void 대여용_상품이_재고_N개로_등록되어_있다(int stockQuantity) {
        helper.대여용_상품을_재고와_함께_등록한다(stockQuantity);
    }

    @Given("예약이 1건 존재한다")
    public void 예약이_1건_존재한다() {
        helper.예약을_조회한다();
    }

    @Given("판매용 상품이 등록되어 있다")
    public void 판매용_상품이_등록되어_있다() {
        helper.판매용_상품을_등록한다();
    }

    // ==================== When ====================

    @When("관리자가 해당 상품 {int}개를 예약에 연결하여 대여한다")
    public void 관리자가_해당_상품_N개를_예약에_연결하여_대여한다(int quantity) {
        helper.예약에_연결하여_대여를_요청한다(quantity);
    }

    @When("관리자가 해당 상품 {int}개 대여를 요청한다")
    public void 관리자가_해당_상품_N개_대여를_요청한다(int quantity) {
        helper.대여를_요청한다(quantity);
    }

    @When("관리자가 해당 상품 대여를 요청한다")
    public void 관리자가_해당_상품_대여를_요청한다() {
        helper.대여를_요청한다(1);
    }

    @When("관리자가 존재하지 않는 상품 대여를 요청한다")
    public void 관리자가_존재하지_않는_상품_대여를_요청한다() {
        helper.존재하지_않는_상품_대여를_요청한다();
    }

    @When("관리자가 예약 없이 해당 상품 {int}개를 대여한다")
    public void 관리자가_예약_없이_해당_상품_N개를_대여한다(int quantity) {
        helper.예약_없이_대여를_요청한다(quantity);
    }

    // ==================== Then ====================

    @Then("대여 내역을 조회하면 확인할 수 있다")
    public void 대여_내역을_조회하면_확인할_수_있다() {
        helper.대여_내역을_조회하여_검증한다();
    }

    @Then("대여 기록은 생성되지 않는다")
    public void 대여_기록은_생성되지_않는다() {
        helper.대여_기록이_생성되지_않았는지_검증한다();
    }

    @Then("해당 대여를 조회하면 미반납 상태이다")
    public void 해당_대여를_조회하면_미반납_상태이다() {
        helper.대여를_조회하여_미반납_상태인지_검증한다();
    }

    @Then("상품을 조회하면 재고가 {int}개이다")
    public void 상품을_조회하면_재고가_N개이다(int expectedStock) {
        helper.상품을_조회하여_재고를_검증한다(expectedStock);
    }

    @Then("상품 재고는 {int}개로 유지된다")
    public void 상품_재고는_N개로_유지된다(int expectedStock) {
        helper.상품_재고가_유지되는지_검증한다(expectedStock);
    }

    @Then("해당 대여를 조회하면 예약과 연결되어 있지 않다")
    public void 해당_대여를_조회하면_예약과_연결되어_있지_않다() {
        helper.대여를_조회하여_예약과_연결되지_않았는지_검증한다();
    }
}