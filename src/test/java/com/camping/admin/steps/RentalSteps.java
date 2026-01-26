package com.camping.admin.steps;

import com.camping.admin.api.RentalApi;
import com.camping.admin.common.TestContext;
import com.camping.admin.common.TestData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class RentalSteps {

    private Long productId;
    private Integer quantity;

    // === Background ===

    @Given("관리자가 로그인되어 있다")
    public void 관리자가_로그인되어_있다() {
        assertThat(TestContext.getAdminToken()).isNotNull();
    }

    // === Given: 상품 설정 ===

    @Given("대여 가능한 상품 {string}이 존재한다")
    public void 대여_가능한_상품이_존재한다(String productName) {
        this.productId = TestData.getProductId(productName);
        TestContext.setProductId(this.productId);
    }

    @Given("판매 전용 상품 {string}이 존재한다")
    public void 판매_전용_상품이_존재한다(String productName) {
        this.productId = TestData.getProductId(productName);
        TestContext.setProductId(this.productId);
    }

    // === Given: 예약 설정 ===

    @And("고객 {string}의 예약이 존재한다")
    public void 고객의_예약이_존재한다(String customerName) {
        Long reservationId = TestData.getReservationId(customerName);
        TestContext.setReservationId(reservationId);
    }

    // === When: 대여 생성 ===

    @When("관리자가 해당 고객에게 {string} {int}개를 대여하면")
    public void 관리자가_해당_고객에게_대여하면(String productName, int quantity) {
        this.productId = TestData.getProductId(productName);
        this.quantity = quantity;
        Response response = RentalApi.대여를_생성한다(
                TestContext.getAdminToken(),
                productId,
                quantity,
                TestContext.getReservationId()
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 워크인 고객에게 {string} {int}개를 대여하면")
    public void 관리자가_워크인_고객에게_대여하면(String productName, int quantity) {
        this.productId = TestData.getProductId(productName);
        this.quantity = quantity;
        Response response = RentalApi.대여를_생성한다(
                TestContext.getAdminToken(),
                productId,
                quantity,
                null  // 워크인은 예약 없음
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 존재하지 않는 상품으로 대여하면")
    public void 관리자가_존재하지_않는_상품으로_대여하면() {
        this.productId = TestData.PRODUCT_NOT_FOUND_ID;
        this.quantity = 1;
        Response response = RentalApi.대여를_생성한다(
                TestContext.getAdminToken(),
                productId,
                quantity,
                null
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 {string}으로 대여하면")
    public void 관리자가_상품으로_대여하면(String productName) {
        this.productId = TestData.getProductId(productName);
        this.quantity = 1;
        Response response = RentalApi.대여를_생성한다(
                TestContext.getAdminToken(),
                productId,
                quantity,
                null
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 {string} {int}개를 대여하면")
    public void 관리자가_수량만큼_대여하면(String productName, int quantity) {
        this.productId = TestData.getProductId(productName);
        this.quantity = quantity;
        Response response = RentalApi.대여를_생성한다(
                TestContext.getAdminToken(),
                productId,
                quantity,
                null
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 존재하지 않는 예약으로 대여하면")
    public void 관리자가_존재하지_않는_예약으로_대여하면() {
        this.quantity = 1;
        Response response = RentalApi.대여를_생성한다(
                TestContext.getAdminToken(),
                productId,
                quantity,
                TestData.RESERVATION_NOT_FOUND_ID
        );
        TestContext.setLastResponse(response);
    }

    // === Then: 결과 검증 ===

    @Then("대여가 성공적으로 생성된다")
    public void 대여가_성공적으로_생성된다() {
        Response response = TestContext.getLastResponse();
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getLong("productId")).isEqualTo(productId);
        assertThat(response.jsonPath().getInt("quantity")).isEqualTo(quantity);
        assertThat(response.jsonPath().getBoolean("isReturned")).isFalse();
    }

    @Then("대여가 실패한다")
    public void 대여가_실패한다() {
        Response response = TestContext.getLastResponse();
        assertThat(response.statusCode()).isGreaterThanOrEqualTo(400);
    }
}
