package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.DisplayName;

import static com.camping.admin.helper.CommonContext.lastResponse;
import static com.camping.admin.helper.RequestSender.get;
import static com.camping.admin.helper.RequestSender.post;
import static com.camping.admin.helper.ResponseValidator.*;
import static com.camping.admin.helper.factory.ProductFactory.productOf;
import static com.camping.admin.helper.factory.RentalRequestFactory.createRentalRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("대여 생성 테스트")
public class RentalSteps {

    public static final String API_PATH_CREATE_RENTAL = "/admin/rentals";
    public static final int DEFAULT_RENTAL_QUANTITY = 1;

    Long reservationId;
    Long productId;

    @Given("사용자가 예약을 했다")
    public void 예약이_있다() {
        reservationId = 1L; // data.sql의 예약 데이터 사용
    }

    @And("제품의 재고가 {int}개 있다")
    public void 제품의_재고가_있다(Integer stockQuantity) {
        var product = productOf(stockQuantity);
        productId = product.getId();
    }

    @And("제품이 있다")
    public void 제품이_있다() {
        productId = 1L; // data.sql의 상품 데이터 사용
    }

    @When("사용자가 예약을 하지 않았다")
    public void 사용자가_예약을_하지_않았다() {
        reservationId = null;
    }

    @When("사용자가 제품을 {int}개 대여한다")
    public void 사용자가_제품을_대여한다(Integer quantity) {
        lastResponse = post(API_PATH_CREATE_RENTAL,
                createRentalRequest(reservationId, productId, quantity));
    }

    @When("사용자가 존재하지 않는 예약 정보로 제품을 대여한다")
    public void 사용자가_존재하지_않는_예약_정보로_제품을_대여한다() {
        reservationId = 999L;
        lastResponse = post(API_PATH_CREATE_RENTAL,
                createRentalRequest(reservationId, productId, DEFAULT_RENTAL_QUANTITY));
    }

    @When("사용자가 재고가 부족한 제품을 대여한다")
    public void 사용자가_재고가_부족한_제품을_대여한다() {
        var product = productOf(20);
        productId = product.getId();

        lastResponse = post(API_PATH_CREATE_RENTAL,
                createRentalRequest(reservationId, productId, 21)); // 현재 재고보다 1개 더 요청
    }

    @Then("대여에 성공했다")
    public void 대여에_성공했다() {
        isCreated(lastResponse);
    }

    @And("대여 상태가 '대여중'이다") // TODO: 3단계 리팩터링에서 대여중과 false를 매핑시키는 enum 추가하기
    public void 대여상태가_대여중_이다() {
        lastResponse.then()
                .body("isReturned", equalTo(false));
    }

    @And("대여된 제품의 재고가 {int}개로 감소했다")
    public void 제품_재고가_감소된다(Integer quantity) {
        var products = get("/admin/products");

        // productId에 해당하는 제품의 stockQuantity 확인
        var actualStockQuantity = products.jsonPath()
                .getInt("find { it.id == " + productId + " }.stockQuantity");

        assertThat(actualStockQuantity).isEqualTo(quantity);
    }

    @Then("대여 요청이 실패한다")
    public void 대여_요청이_실패한다() {
        isBadRequest(lastResponse);
    }

    @Then("재고 부족으로 대여 요청이 실패한다")
    public void 재고_부족으로_대여_요청이_실패한다() {
        isConflict(lastResponse);
    }

    @And("대여 조회시 예약 정보가 존재하지 않는다")
    public void 대여_조회시_예약_정보가_존재하지_않는다() {
        lastResponse.then()
                .body("reservationId", equalTo(null));
    }

    @And("대여 조회시 예약 정보가 존재한다")
    public void 대여_조회시_예약_정보가_존재한다() {
        lastResponse.then()
                .body("id", notNullValue());
    }

    @When("사용자가 존재하지 않는 제품을 대여한다")
    public void 사용자가존재하지않는제품을대여한다() {
        productId = 999L;
        lastResponse = post(API_PATH_CREATE_RENTAL,
                createRentalRequest(reservationId, productId, DEFAULT_RENTAL_QUANTITY));
    }

    @When("사용자가 판매용 상품을 대여한다")
    public void 사용자가판매용상품을대여한다() {
        productId = 2L;
        lastResponse = post(API_PATH_CREATE_RENTAL,
                createRentalRequest(reservationId, productId, DEFAULT_RENTAL_QUANTITY));
    }
}
