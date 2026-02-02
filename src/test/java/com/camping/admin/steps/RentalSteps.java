package com.camping.admin.steps;

import com.camping.admin.api.RentalApi;
import com.camping.admin.common.TestContext;
import com.camping.admin.common.TestData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.camping.admin.common.TestContext.Key.*;

public class RentalSteps {

    // ===== Given =====

    @Given("고객의 예약이 존재한다")
    public void 고객의_예약이_존재한다() {
        TestContext.set(RESERVATION_ID, TestData.RESERVATION_ID);
        TestContext.set(PRODUCT_ID, TestData.RENTAL_PRODUCT_ID);
    }

    @Given("대여 중인 장비가 있다")
    public void 대여_중인_장비가_있다() {
        Response response = RentalApi.대여_생성(TestContext.getAdminToken(), TestData.RENTAL_PRODUCT_ID, 1);
        TestContext.set(RENTAL_RECORD_ID, response.jsonPath().getLong("id"));
    }

    @Given("판매 상품이 존재한다")
    public void 판매_상품이_존재한다() {
        TestContext.set(PRODUCT_ID, TestData.SALE_PRODUCT_ID);
    }

    @Given("반납 완료된 장비가 있다")
    public void 반납_완료된_장비가_있다() {
        Response createResponse = RentalApi.대여_생성(TestContext.getAdminToken(), TestData.RENTAL_PRODUCT_ID, 1);
        Long rentalRecordId = createResponse.jsonPath().getLong("id");
        RentalApi.반납(TestContext.getAdminToken(), rentalRecordId);
        TestContext.set(RENTAL_RECORD_ID, rentalRecordId);
    }

    // ===== When: 인증 O =====

    @When("관리자가 예약 고객에게 장비를 대여한다")
    public void 관리자가_예약_고객에게_장비를_대여한다() {
        Response response = RentalApi.대여_생성_with_예약(
                TestContext.getAdminToken(),
                TestContext.getId(PRODUCT_ID),
                1,
                TestContext.getId(RESERVATION_ID));
        TestContext.setLastResponse(response);
    }

    @When("관리자가 워크인 고객에게 장비를 대여한다")
    public void 관리자가_워크인_고객에게_장비를_대여한다() {
        Response response = RentalApi.대여_생성(TestContext.getAdminToken(), TestData.RENTAL_PRODUCT_ID, 1);
        TestContext.setLastResponse(response);
    }

    @When("관리자가 반납 처리한다")
    public void 관리자가_반납_처리한다() {
        Response response = RentalApi.반납(TestContext.getAdminToken(), TestContext.getId(RENTAL_RECORD_ID));
        TestContext.setLastResponse(response);
    }

    @When("관리자가 대여 목록을 조회한다")
    public void 관리자가_대여_목록을_조회한다() {
        Response response = RentalApi.목록_조회(TestContext.getAdminToken());
        TestContext.setLastResponse(response);
    }

    @When("관리자가 미반납 장비 목록을 조회한다")
    public void 관리자가_미반납_장비_목록을_조회한다() {
        Response response = RentalApi.미반납_목록_조회(TestContext.getAdminToken());
        TestContext.setLastResponse(response);
    }

    @When("관리자가 존재하지 않는 상품을 대여한다")
    public void 관리자가_존재하지_않는_상품을_대여한다() {
        Response response = RentalApi.대여_생성(TestContext.getAdminToken(), TestData.NOT_FOUND_ID, 1);
        TestContext.setLastResponse(response);
    }

    @When("관리자가 판매 상품을 대여한다")
    public void 관리자가_판매_상품을_대여한다() {
        Response response = RentalApi.대여_생성(TestContext.getAdminToken(), TestContext.getId(PRODUCT_ID), 1);
        TestContext.setLastResponse(response);
    }

    @When("관리자가 재고보다 많은 수량을 대여한다")
    public void 관리자가_재고보다_많은_수량을_대여한다() {
        Response response = RentalApi.대여_생성(TestContext.getAdminToken(), TestData.RENTAL_PRODUCT_ID, 99999);
        TestContext.setLastResponse(response);
    }

    @When("관리자가 존재하지 않는 예약으로 대여한다")
    public void 관리자가_존재하지_않는_예약으로_대여한다() {
        Response response = RentalApi.대여_생성_with_예약(TestContext.getAdminToken(), TestData.RENTAL_PRODUCT_ID, 1, TestData.NOT_FOUND_ID);
        TestContext.setLastResponse(response);
    }

    @When("관리자가 존재하지 않는 대여이력을 반납한다")
    public void 관리자가_존재하지_않는_대여이력을_반납한다() {
        Response response = RentalApi.반납(TestContext.getAdminToken(), TestData.NOT_FOUND_ID);
        TestContext.setLastResponse(response);
    }

    // ===== When: 인증 X =====

    @When("관리자 권한 없이 장비 대여를 등록한다")
    public void 관리자_권한_없이_장비_대여를_등록한다() {
        Response response = RentalApi.대여_생성_인증없이(1L, 1);
        TestContext.setLastResponse(response);
    }

    @When("관리자 권한 없이 반납 처리한다")
    public void 관리자_권한_없이_반납_처리한다() {
        Response response = RentalApi.반납_인증없이(1L);
        TestContext.setLastResponse(response);
    }

    @When("관리자 권한 없이 대여 목록을 조회한다")
    public void 관리자_권한_없이_대여_목록을_조회한다() {
        Response response = RentalApi.목록_조회_인증없이();
        TestContext.setLastResponse(response);
    }
}