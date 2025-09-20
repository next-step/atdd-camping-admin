package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.DisplayName;

import static com.camping.admin.helper.CommonContext.lastResponse;
import static com.camping.admin.helper.ProductFactory.productOf;
import static com.camping.admin.helper.RequestFactory.createRentalRequest;
import static com.camping.admin.helper.RequestSender.get;
import static com.camping.admin.helper.RequestSender.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.*;

@DisplayName("대여 생성 테스트")
public class RentalSteps {

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
        lastResponse = post("/admin/rentals",
                createRentalRequest(reservationId, productId, quantity));
    }

    @Then("대여에 성공했다")
    public void 대여에_성공했다() {
        lastResponse.then()
                .statusCode(CREATED.value());
    }

    @And("대여 ID가 생성된다")
    public void 대여_ID가_생성된다() {
        lastResponse.then()
                .body("id", notNullValue());
    }

    @And("대여 상태가 '대여중'이다") // TODO: 3단계 리팩터링에서 대여중과 false를 매핑시키는 enum 추가하기
    public void 대여상태가_대여중_이다() {
        lastResponse.then()
                .body("isReturned", equalTo(false));
    }

    @And("대여된 제품의 재고가 {int}개로 감소했다")
    public void 제품_재고가_감소된다(Integer quantity) {
        var products = get("/admin/products")
                .then()
                .log().all()
                .extract();

        // productId에 해당하는 제품의 stockQuantity 확인
        var actualStockQuantity = products.jsonPath()
                .getInt("find { it.id == " + productId + " }.stockQuantity");

        assertThat(actualStockQuantity).isEqualTo(quantity);
    }

    @When("사용자가 존재하지 않는 제품을 대여 요청한다")
    public void 사용자가_존재하지_않는_제품을_대여_요청한다() {
        Long nonExistentProductId = 999L;
        lastResponse = post("/admin/rentals",
                createRentalRequest(reservationId, nonExistentProductId, 1));
    }

    @Then("대여 요청이 실패한다")
    public void 대여_요청이_실패한다() {
        lastResponse.then()
                .statusCode(BAD_REQUEST.value());
    }

    @Then("재고 부족으로 대여 요청이 실패한다")
    public void 재고_부족으로_대여_요청이_실패한다() {
        lastResponse.then()
                .statusCode(CONFLICT.value());
    }

    @And("판매용 제품이 있다.")
    public void 판매용_제품이_있다() {
        productId = 2L; // data.sql의 판매용 제품 (장작팩)
    }

    @When("사용자가 판매용 제품을 대여 요청한다")
    public void 사용자가_판매용_제품을_대여_요청한다() {
        lastResponse = post("/admin/rentals",
                createRentalRequest(reservationId, productId, 1));
    }

    // 재고가 부족한 제품으로 대여 요청 시나리오
    @And("재고가 부족한 대여 제품이 있다.")
    public void 재고가_부족한_대여_제품이_있다() {
        productId = 5L; // data.sql의 테이블 (재고 10개)
    }

    @When("사용자가 재고보다 많은 수량을 대여 요청한다")
    public void 사용자가_재고보다_많은_수량을_대여_요청한다() {
        lastResponse = post("/admin/rentals",
                createRentalRequest(reservationId, productId, 999)); // 재고 10개보다 많은 999개 요청
    }

    @When("사용자가 존재하지 않는 예약으로 제품을 대여 요청한다")
    public void 사용자가_존재하지_않는_예약으로_제품을_대여_요청한다() {
        Long nonExistentReservationId = 999L;
        lastResponse = post("/admin/rentals",
                createRentalRequest(nonExistentReservationId, productId, 1));
    }

    @And("예약 ID가 null이다")
    public void 예약_ID가_null이다() {
        lastResponse.then()
                .body("reservationId", equalTo(null));
    }
}