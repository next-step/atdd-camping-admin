package com.camping.admin.steps;

import com.camping.admin.support.ApiHelper;
import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class RentalStepDefs {

    @When("관리자가 대여 목록을 조회한다")
    public void 관리자가대여목록을조회한다() {
        ApiHelper.makeAuthenticatedRequest("GET", "/admin/rentals", null);
    }

    @Then("대여 목록이 반환된다")
    public void 대여목록이반환된다() {
        CommonContext.lastResponse.then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @And("대여 정보에는 상품과 수량 정보가 포함된다")
    public void 대여정보에는상품과수량정보가포함된다() {
        CommonContext.lastResponse.then()
                .body("[0].productId", notNullValue())
                .body("[0].quantity", notNullValue());
    }

    @When("권한 없는 사용자가 대여 목록을 조회한다")
    public void 권한없는사용자가대여목록을조회한다() {
        ApiHelper.makeUnauthenticatedRequest("GET", "/admin/rentals", null);
    }


    @Then("대여 생성에 실패한다")
    public void 대여생성에실패한다() {
        CommonContext.lastResponse.then().statusCode(404);
    }

    @When("관리자가 상품 {int}번을 예약 {int}번과 함께 {int}개 대여한다")
    public void 관리자가상품번을예약번과함께개대여한다(int productId, int reservationId, int quantity) {
        Map<String, Object> requestBody = Map.of(
                "productId", productId,
                "quantity", quantity,
                "reservationId", reservationId
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/rentals", requestBody);
    }

    @Then("대여 수량이 {int}인 대여가 생성된다")
    public void 대여수량이인대여가생성된다(int expectedQuantity) {
        CommonContext.lastResponse.then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("quantity", equalTo(expectedQuantity));
    }

    @When("관리자가 상품 {int}번을 예약 없이 {int}개 대여한다")
    public void 관리자가상품번을예약없이개대여한다(int productId, int quantity) {
        Map<String, Object> requestBody = Map.of(
                "productId", productId,
                "quantity", quantity
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/rentals", requestBody);
    }

    @When("관리자가 존재하지 않는 상품 {int}번으로 대여를 생성한다")
    public void 관리자가존재하지않는상품번으로대여를생성한다(int productId) {
        Map<String, Object> requestBody = Map.of(
                "productId", productId,
                "quantity", 1
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/rentals", requestBody);
    }

    @When("관리자가 상품 {int}번을 존재하지 않는 예약 {int}번과 함께 대여한다")
    public void 관리자가상품번을존재하지않는예약번과함께대여한다(int productId, int reservationId) {
        Map<String, Object> requestBody = Map.of(
                "productId", productId,
                "quantity", 1,
                "reservationId", reservationId
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/rentals", requestBody);
    }

    @When("관리자가 상품 {int}번을 음수 수량으로 대여한다")
    public void 관리자가상품번을음수수량으로대여한다(int productId) {
        Map<String, Object> requestBody = Map.of(
                "productId", productId,
                "quantity", -1
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/rentals", requestBody);
    }

    @When("관리자가 상품 {int}번을 {int}개 대여한다")
    public void 관리자가상품번을개대여한다(int productId, int quantity) {
        Map<String, Object> requestBody = Map.of(
                "productId", productId,
                "quantity", quantity
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/rentals", requestBody);
    }

    @When("관리자가 상품 ID 없이 대여를 생성한다")
    public void 관리자가상품ID없이대여를생성한다() {
        Map<String, Object> requestBody = Map.of(
                "quantity", 1
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/rentals", requestBody);
    }

    @When("권한 없는 사용자가 상품 {int}번으로 대여를 생성한다")
    public void 권한없는사용자가상품번으로대여를생성한다(int productId) {
        Map<String, Object> requestBody = Map.of(
                "productId", productId,
                "quantity", 1
        );
        ApiHelper.makeUnauthenticatedRequest("POST", "/admin/rentals", requestBody);
    }
}
