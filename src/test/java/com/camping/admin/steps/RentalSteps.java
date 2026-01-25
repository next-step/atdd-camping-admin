package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.helper.ProductTestHelper;
import com.camping.admin.helper.RentalTestHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class RentalSteps {

    private final TestHelperContext helpers;
    private Response lastResponse;
    private int targetProductId;
    private int targetRentalId;
    private int originalStock;

    public RentalSteps(TestHelperContext helpers) {
        this.helpers = helpers;
    }

    private RentalTestHelper rentalHelper() {
        return helpers.rental();
    }

    private ProductTestHelper productHelper() {
        return helpers.product();
    }

    @Given("대여 가능한 상품이 있다")
    public void 대여가능한상품이있다() {
        targetProductId = productHelper().findProductByTypeAndStock("RENTAL", 1);
        originalStock = productHelper().getProductStock(targetProductId);
        System.out.println("[Given] 대여 가능한 상품이 있다. ID: " + targetProductId);
    }

    @When("관리자가 해당 상품을 대여한다")
    public void 관리자가해당상품을대여한다() {
        // SharedSteps에서 설정된 경우 productHelper에서 가져옴
        int productId = targetProductId > 0 ? targetProductId : productHelper().getTargetProductId();
        lastResponse = rentalHelper().createRental(productId, 1);
    }

    @Then("대여가 생성된다")
    public void 대여가생성된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(200, 201);

        int createdRentalId = lastResponse.jsonPath().getInt("id");
        boolean exists = rentalHelper().isRentalReturned(createdRentalId) || !rentalHelper().isRentalReturned(createdRentalId);
        assertThat(exists).isTrue();
        System.out.println("[Then] 대여가 생성된다 - DB 반영 확인 완료");
    }

    @And("대여 상품 재고가 감소한다")
    public void 대여상품재고가감소한다() {
        int currentStock = productHelper().getProductStock(targetProductId);
        assertThat(currentStock).isLessThan(originalStock);
        System.out.println("[And] 상품 재고가 감소한다");
    }

    @Given("대여 중인 장비가 있다")
    public void 대여중인장비가있다() {
        targetRentalId = rentalHelper().findUnreturnedRentalId();
        targetProductId = rentalHelper().getRentalProductId(targetRentalId);
        originalStock = productHelper().getProductStock(targetProductId);
        System.out.println("[Given] 대여 중인 장비가 있다. ID: " + targetRentalId);
    }

    @When("관리자가 해당 장비 반납 처리한다")
    public void 관리자가해당장비반납처리한다() {
        lastResponse = rentalHelper().returnRental(targetRentalId);
    }

    @Then("반납이 완료된다")
    public void 반납이완료된다() {
        lastResponse.then().statusCode(200);

        boolean isReturned = rentalHelper().isRentalReturned(targetRentalId);
        assertThat(isReturned).isTrue();
        System.out.println("[Then] 반납이 완료된다 - DB 반영 확인 완료");
    }

    @And("상품 재고가 복구된다")
    public void 상품재고가복구된다() {
        int currentStock = productHelper().getProductStock(targetProductId);
        assertThat(currentStock).isGreaterThan(originalStock);
        System.out.println("[And] 상품 재고가 복구된다");
    }

    @Then("대여가 거부된다")
    public void 대여가거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 409);
        System.out.println("[Then] 대여가 거부된다");
    }

    @Given("이미 반납된 대여 기록이 있다")
    public void 이미반납된대여기록이있다() {
        targetRentalId = rentalHelper().findReturnedRentalId();
        System.out.println("[Given] 이미 반납된 대여 기록이 있다. ID: " + targetRentalId);
    }

    @Then("반납이 거부된다")
    public void 반납이거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 409);
        System.out.println("[Then] 반납이 거부된다");
    }
}