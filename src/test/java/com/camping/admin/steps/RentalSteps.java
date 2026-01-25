package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.factory.RentalFactory;
import com.camping.admin.factory.ReservationFactory;
import com.camping.admin.support.TestApiSupport;
import com.camping.admin.api.TestContext;
import com.camping.admin.support.TestRepositorySupport;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;


public class RentalSteps {

    public static final String SITE_NUMBER = "A-001";
    public static final String CONFIRMED = "CONFIRMED";


    @LocalServerPort
    private int port;

    @Autowired
    private TestContext testContext;

    @Autowired
    private TestApiSupport api;

    @Autowired
    private TestRepositorySupport repository;

    @Autowired
    private RentalFactory rentalFactory;

    @Autowired
    private ReservationFactory reservationFactory;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    // ==================== Given Steps ====================

    @Given("{string} 대여 상품이 재고 {int}개로 등록되어 있다")
    public void 대여_상품이_등록되어_있다(String productName, int stockQuantity) {
        Product saved = rentalFactory.createRental(productName, stockQuantity, ProductType.RENTAL);
        testContext.getProduct().setId(saved.getId());
        testContext.getProduct().setName(productName);
    }

    @Given("{string} 판매 상품이 재고 {int}개로 등록되어 있다")
    public void 판매_상품이_등록되어_있다(String productName, int stockQuantity) {
        Product saved = rentalFactory.createRental(productName, stockQuantity, ProductType.SALE);
        testContext.getProduct().setId(saved.getId());
        testContext.getProduct().setName(productName);
    }

    @Given("{string} 고객의 예약이 존재한다")
    public void 고객의_예약이_존재한다(String customerName) {
        Reservation saved = reservationFactory.createReservation(customerName, SITE_NUMBER, CONFIRMED);
        testContext.getReservation().setId(saved.getId());
    }

    // ==================== When Steps ====================

    @When("관리자가 해당 예약에 {string} 상품 {int}개를 대여한다")
    public void 예약에_상품을_대여한다(String productName, int quantity) {
        Long productId = findProductIdByName(productName);
        api.rental().대여_생성(productId, quantity, testContext.getReservation().getId());
    }

    @When("관리자가 예약 없이 {string} 상품 {int}개를 대여한다")
    public void 예약_없이_상품을_대여한다(String productName, int quantity) {
        Long productId = findProductIdByName(productName);
        api.rental().대여_생성(productId, quantity, null);
    }

    @When("관리자가 존재하지 않는 상품을 대여한다")
    public void 존재하지_않는_상품을_대여한다() {
        api.rental().대여_생성(99999L, 1, null);
    }

    @When("관리자가 존재하지 않는 예약에 {string} 상품 {int}개를 대여한다")
    public void 존재하지_않는_예약에_대여한다(String productName, int quantity) {
        Long productId = findProductIdByName(productName);
        api.rental().대여_생성(productId, quantity, 99999L);
    }

    // ==================== Then Steps ====================

    @Then("대여가 정상적으로 생성된다")
    public void 대여가_정상적으로_생성된다() {
        assertThat(testContext.getResponse().statusCode())
                .as("API 응답 상태코드")
                .isEqualTo(HttpStatus.CREATED.value());

        assertThat(testContext.getResponse().jsonPath().getLong("id"))
                .as("생성된 대여 ID")
                .isPositive();
    }

    @And("{string} 상품의 재고가 {int}개로 감소한다")
    public void 상품의_재고가_감소한다(String productName, int expectedStock) {
        Product product = repository.product().findByName(productName)
                .orElseThrow(() -> new AssertionError("상품을 찾을 수 없습니다: " + productName));

        assertThat(product.getStockQuantity())
                .as("상품 재고")
                .isEqualTo(expectedStock);
    }

    @And("{string} 상품의 재고는 {int}개로 유지된다")
    public void 상품의_재고가_유지된다(String productName, int expectedStock) {
        Product product = repository.product().findByName(productName)
                .orElseThrow(() -> new AssertionError("상품을 찾을 수 없습니다: " + productName));

        assertThat(product.getStockQuantity())
                .as("상품 재고")
                .isEqualTo(expectedStock);
    }

    @Then("재고가 부족하다는 오류가 발생한다")
    public void 재고_부족_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, "재고");
    }

    @Then("대여 불가 상품이라는 오류가 발생한다")
    public void 대여_불가_상품_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, "rental");
    }

    @Then("상품을 찾을 수 없다는 오류가 발생한다")
    public void 상품을_찾을_수_없음_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, "product");
    }

    @Then("수량은 1개 이상이어야 한다는 오류가 발생한다")
    public void 수량_오류() {
        assertErrorResponse(HttpStatus.BAD_REQUEST, "수량");
    }

    // ==================== Helper Methods ====================

    private Long findProductIdByName(String productName) {
        return repository.product().findByName(productName)
                .map(Product::getId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productName));
    }

    private void assertErrorResponse(HttpStatus expectedStatus, String expectedMessagePart) {
        assertThat(testContext.getResponse().statusCode())
                .as("API 응답 상태코드")
                .isEqualTo(expectedStatus.value());

        String responseBody = testContext.getResponse().body().asString();
        assertThat(responseBody)
                .as("오류 메시지 포함 여부")
                .containsIgnoringCase(expectedMessagePart);
    }
}
