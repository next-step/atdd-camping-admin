package com.camping.admin.steps;

import com.camping.admin.support.ApiHelper;
import com.camping.admin.support.CommonContext;
import com.camping.admin.support.DataTableHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class RentalStepDefs {

    @When("관리자가 대여 목록을 조회한다")
    public void 관리자가대여목록을조회한다() {
        ApiHelper.makeAuthenticatedRequest("GET", "/admin/rentals", null);
    }

    @When("관리자가 다음 정보로 대여를 생성한다:")
    public void 관리자가다음정보로대여를생성한다(DataTable dataTable) {
        Map<String, Object> requestBody = DataTableHelper.buildRequestBodyFromDataTable(dataTable, false);
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/rentals", requestBody);
    }

    @Then("대여 목록이 반환된다")
    public void 대여목록이반환된다() {
        CommonContext.getLastResponse().then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @And("대여 정보에는 상품과 수량 정보가 포함된다")
    public void 대여정보에는상품과수량정보가포함된다() {
        CommonContext.getLastResponse().then()
                .body("[0].productId", notNullValue())
                .body("[0].quantity", notNullValue());
    }

    @And("생성된 대여 정보가 반환된다")
    public void 생성된대여정보가반환된다() {
        CommonContext.getLastResponse().then()
                .body("id", notNullValue())
                .body("productId", notNullValue())
                .body("quantity", notNullValue());
    }

    @And("대여 수량은 {int}이다")
    public void 대여수량은이다(int expectedQuantity) {
        CommonContext.getLastResponse().then()
                .body("quantity", equalTo(expectedQuantity));
    }

    @And("대여의 예약 ID는 null이다")
    public void 대여의예약ID는null이다() {
        CommonContext.getLastResponse().then()
                .body("reservationId", nullValue());
    }

    @When("권한 없는 사용자가 다음 정보로 대여를 생성한다:")
    public void 권한없는사용자가다음정보로대여를생성한다(DataTable dataTable) {
        Map<String, Object> requestBody = DataTableHelper.buildRequestBodyFromDataTable(dataTable, false);
        ApiHelper.makeUnauthenticatedRequest("POST", "/admin/rentals", requestBody);
    }

    @When("권한 없는 사용자가 대여 목록을 조회한다")
    public void 권한없는사용자가대여목록을조회한다() {
        ApiHelper.makeUnauthenticatedRequest("GET", "/admin/rentals", null);
    }
}
