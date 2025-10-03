package com.camping.admin.steps;

import com.camping.admin.helpers.ApiHelper;
import com.camping.admin.helpers.RentalTestHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import static org.assertj.core.api.Assertions.assertThat;

public class RentalSteps {

    private Integer initialStock;

    @Given("대여 가능한 상품이 등록되어 있다")
    public void rentalProductExists() {
        Long productId = RentalTestHelper.createRentalProduct("텐트", 10);
        RentalTestHelper.setProductId(productId);
        initialStock = RentalTestHelper.getProductStock(productId);
    }

    @Given("판매 타입 상품이 등록되어 있다")
    public void saleProductExists() {
        Long productId = RentalTestHelper.createSaleProduct("음료수", 20);
        RentalTestHelper.setProductId(productId);
    }

    @Given("대여 기록이 존재한다")
    public void rentalRecordExists() {
        Long productId = RentalTestHelper.createRentalProduct("텐트", 10);
        RentalTestHelper.setProductId(productId);
        initialStock = RentalTestHelper.getProductStock(productId);

        Long rentalId = RentalTestHelper.createAndGetRentalId(productId, 2);
        RentalTestHelper.setRentalId(rentalId);
    }

    @Given("반납 완료된 대여 기록이 존재한다")
    public void returnedRentalRecordExists() {
        Long productId = RentalTestHelper.createRentalProduct("텐트", 10);
        Long rentalId = RentalTestHelper.createAndGetRentalId(productId, 2);

        RentalTestHelper.returnRental(rentalId);
        RentalTestHelper.setRentalId(rentalId);
    }

    @Given("대여 기록이 {int}개 존재한다")
    public void multipleRentalRecordsExist(int count) {
        Long productId = RentalTestHelper.createRentalProduct("텐트", 100);
        for (int i = 0; i < count; i++) {
            RentalTestHelper.createRental(productId, 1, null);
        }
    }

    @When("관리자가 상품을 대여한다")
    public void createRental() {
        Long productId = RentalTestHelper.getProductId();
        Response response = RentalTestHelper.createRental(productId, 2, null);
        RentalTestHelper.setResponse(response);
    }

    @When("관리자가 판매 상품을 대여하려고 한다")
    public void createRentalForSaleProduct() {
        Long productId = RentalTestHelper.getProductId();
        Response response = RentalTestHelper.createRental(productId, 1, null);
        RentalTestHelper.setResponse(response);
    }

    @When("관리자가 대여 상품을 반납한다")
    public void returnRental() {
        Long rentalId = RentalTestHelper.getRentalId();
        Response response = RentalTestHelper.returnRental(rentalId);
        RentalTestHelper.setResponse(response);
    }

    @When("관리자가 이미 반납된 상품을 반납하려고 한다")
    public void returnAlreadyReturnedRental() {
        Long rentalId = RentalTestHelper.getRentalId();
        Response response = RentalTestHelper.returnRental(rentalId);
        RentalTestHelper.setResponse(response);
    }

    @When("관리자가 대여 기록 목록을 조회한다")
    public void getAllRentals() {
        Response response = RentalTestHelper.getAllRentals();
        RentalTestHelper.setResponse(response);
    }

    @Then("대여 등록에 성공한다")
    public void rentalCreatedSuccessfully() {
        RentalTestHelper.getLastResponse().then().statusCode(201);
    }

    @Then("반납 처리에 성공한다")
    public void rentalReturnedSuccessfully() {
        RentalTestHelper.getLastResponse().then().statusCode(200);
    }

    @Then("대여 등록에 실패한다")
    public void rentalCreationFails() {
        Response response = RentalTestHelper.getLastResponse();
        ApiHelper.assertClientError(response);
    }

    @Then("반납 처리에 실패한다")
    public void rentalReturnFails() {
        Response response = RentalTestHelper.getLastResponse();
        ApiHelper.assertClientError(response);
    }

    @Then("{int}개의 대여 기록이 조회된다")
    public void rentalsAreRetrieved(int count) {
        RentalTestHelper.getLastResponse()
                .then()
                .statusCode(200)
                .body("$", Matchers.hasSize(count));
    }

    @Then("대여 기록 목록 조회에 성공한다")
    public void rentalListRetrievedSuccessfully() {
        RentalTestHelper.getLastResponse().then().statusCode(200);
    }

    @And("상품 재고가 감소한다")
    public void productStockDecreased() {
        Long productId = RentalTestHelper.getProductId();
        Integer currentStock = RentalTestHelper.getProductStock(productId);
        assertThat(currentStock).isLessThan(initialStock);
    }

    @And("상품 재고가 증가한다")
    public void productStockIncreased() {
        Long productId = RentalTestHelper.getProductId();
        Integer stockAfterRental = initialStock - 2;
        Integer currentStock = RentalTestHelper.getProductStock(productId);
        assertThat(currentStock).isGreaterThan(stockAfterRental);
    }
}
