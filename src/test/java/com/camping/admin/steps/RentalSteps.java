package com.camping.admin.steps;

import com.camping.admin.api.AuthApi;
import com.camping.admin.api.RentalApi;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class RentalSteps {

    private String adminToken;
    private Response lastResponse;
    private Long productId;
    private Long reservationId;
    private Integer quantity;

    @Before(order = 1)
    public void setupAdminToken() {
        adminToken = AuthApi.관리자_토큰을_발급한다();
    }

    @Given("대여 가능한 상품이 존재한다")
    public void 대여_가능한_상품이_존재한다() {
        // data.sql: ID=1, 랜턴, RENTAL, 재고 20
        this.productId = 1L;
        this.quantity = 2;
    }

    @Given("고객이 캠핑장 예약을 했다")
    public void 고객이_캠핑장_예약을_했다() {
        // data.sql: ID=1, 홍길동, CONFIRMED
        this.reservationId = 1L;
    }

    @When("관리자가 해당 고객에게 장비를 대여하면")
    public void 관리자가_해당_고객에게_장비를_대여하면() {
        lastResponse = RentalApi.대여를_생성한다(adminToken, productId, quantity, reservationId);
    }

    @Then("대여가 성공적으로 생성된다")
    public void 대여가_성공적으로_생성된다() {
        assertThat(lastResponse.statusCode()).isEqualTo(201);
        assertThat(lastResponse.jsonPath().getLong("productId")).isEqualTo(productId);
        assertThat(lastResponse.jsonPath().getInt("quantity")).isEqualTo(quantity);
        assertThat(lastResponse.jsonPath().getBoolean("isReturned")).isFalse();
    }
}
