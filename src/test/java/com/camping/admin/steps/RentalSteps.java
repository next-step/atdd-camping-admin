package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.steps.api.RentalApi;
import com.camping.admin.steps.context.TestContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
public class RentalSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestContext testContext;

    @Autowired
    private RentalApi rentalApi;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @When("관리자가 {string} 상품 {int}개를 대여 처리하면")
    public void adminCreatesRental(String productName, int quantity) {
        testContext.setLastResponse(rentalApi.대여_생성_요청(testContext.getAdminToken(), testContext.getLastProductId(), quantity));
    }

    @When("관리자가 존재하지 않는 상품 ID로 {int}개를 대여 처리하면")
    public void adminCreatesRentalWithInvalidProductId(int quantity) {
        testContext.setLastResponse(rentalApi.대여_생성_요청(testContext.getAdminToken(), 9999L, quantity));
    }

    @And("예약 ID가 존재하지 않는다")
    public void reservationIdDoesNotExist() {
        testContext.setLastReservationId(9999L);
    }

    @And("예약 없이 방문했다")
    public void walkInWithoutReservation() {
        testContext.setLastReservationId(null);
    }

    @When("관리자가 {string} 상품 {int}개를 예약 ID 없이 대여 처리하면")
    public void adminCreatesRentalWithPreparedReservationId(String productName, int quantity) {
        testContext.setLastResponse(rentalApi.대여_생성_요청(testContext.getAdminToken(), testContext.getLastProductId(), quantity, testContext.getLastReservationId()));
    }

    @When("관리자가 {string} 상품 {int}개를 취소된 예약 ID로 요청하면")
    public void adminCreatesRentalWithCancelledReservationId(String productName, int quantity) {
        testContext.setLastResponse(rentalApi.대여_생성_요청(testContext.getAdminToken(), testContext.getLastProductId(), quantity, testContext.getLastReservationId()));
    }

    @Then("대여 기록이 정상적으로 생성된다")
    public void rentalRecordIsCreated() {
        assertThat(testContext.getLastResponse().statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Then("대여 요청이 실패한다")
    public void rentalRequestShouldFail() {
        assertThat(testContext.getLastResponse().statusCode()).isGreaterThanOrEqualTo(400);
    }

    @Then("{string} 상품의 재고는 {int}개가 된다")
    public void productStockShouldBe(String productName, int expectedStock) {
        Product product = productRepository.findById(testContext.getLastProductId()).orElseThrow();
        assertThat(product.getStockQuantity()).isEqualTo(expectedStock);
    }
}