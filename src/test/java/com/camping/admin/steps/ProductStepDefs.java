package com.camping.admin.steps;

import com.camping.admin.support.ApiHelper;
import com.camping.admin.support.CommonContext;
import com.camping.admin.support.DataTableHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class ProductStepDefs {

    @When("관리자가 상품 목록을 조회한다")
    public void 관리자가상품목록을조회한다() {
        ApiHelper.makeAuthenticatedRequest("GET", "/admin/products", null);
    }

    @When("관리자가 다음 정보로 상품을 생성한다:")
    public void 관리자가다음정보로상품을생성한다(DataTable dataTable) {
        Map<String, Object> requestBody = DataTableHelper.buildRequestBodyFromDataTable(dataTable, true);
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/products", requestBody);
    }

    @Then("상품 목록이 반환된다")
    public void 상품목록이반환된다() {
        CommonContext.lastResponse.then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @And("상품 정보에는 이름과 가격 정보가 포함된다")
    public void 상품정보에는이름과가격정보가포함된다() {
        CommonContext.lastResponse.then()
                .body("[0].name", notNullValue())
                .body("[0].price", notNullValue())
                .body("[0].productType", notNullValue());
    }

    @And("생성된 상품 정보가 반환된다")
    public void 생성된상품정보가반환된다() {
        CommonContext.lastResponse.then()
                .body("id", notNullValue())
                .body("name", notNullValue())
                .body("price", notNullValue());
    }

    @And("상품 이름은 {string}이다")
    public void 상품이름은이다(String expectedName) {
        // 고유 접미사를 추가했으므로 이름이 예상 값으로 시작하는지만 확인
        CommonContext.lastResponse.then()
                .body("name", startsWith(expectedName));
    }

    @And("상품 가격은 {int}이다")
    public void 상품가격은이다(int expectedPrice) {
        CommonContext.lastResponse.then()
                .body("price", equalTo(expectedPrice));
    }

    @But("생성된 상품의 이름은 null이다")
    public void 생성된상품의이름은null이다() {
        CommonContext.lastResponse.then()
                .body("name", nullValue());
    }

    @And("생성된 상품의 타입은 {string}이다")
    public void 생성된상품의타입은이다(String expectedType) {
        CommonContext.lastResponse.then()
                .body("productType", equalTo(expectedType));
    }

    @When("권한 없는 사용자가 다음 정보로 상품을 생성한다:")
    public void 권한없는사용자가다음정보로상품을생성한다(DataTable dataTable) {
        Map<String, Object> requestBody = DataTableHelper.buildRequestBodyFromDataTable(dataTable, true);
        ApiHelper.makeUnauthenticatedRequest("POST", "/admin/products", requestBody);
    }

    @When("권한 없는 사용자가 상품 목록을 조회한다")
    public void 권한없는사용자가상품목록을조회한다() {
        ApiHelper.makeUnauthenticatedRequest("GET", "/admin/products", null);
    }

    @Then("새 상품 생성에 성공한다")
    public void 새상품생성에성공한다() {
        CommonContext.lastResponse.then().statusCode(201);
    }

    @Then("상품 생성에 성공한다")
    public void 상품생성에성공한다() {
        CommonContext.lastResponse.then().statusCode(201);
    }

    @When("관리자가 {string} 상품을 {int}원에 생성한다")
    public void 관리자가상품을원에생성한다(String name, int price) {
        Map<String, Object> requestBody = Map.of(
                "name", name + "-" + System.currentTimeMillis(),
                "price", price,
                "stockQuantity", 10,
                "productType", "RENTAL"
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/products", requestBody);
    }

    @Then("상품 이름이 {string}이고 가격이 {int}원인 상품이 생성된다")
    public void 상품이름이이고가격이원인상품이생성된다(String expectedName, int expectedPrice) {
        CommonContext.lastResponse.then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", startsWith(expectedName))
                .body("price", equalTo(expectedPrice));
    }

    @When("관리자가 상품명 없이 상품을 생성한다")
    public void 관리자가상품명없이상품을생성한다() {
        Map<String, Object> requestBody = Map.of(
                "price", 50000,
                "stockQuantity", 10,
                "productType", "RENTAL"
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/products", requestBody);
    }

    @When("관리자가 {string} 상품을 잘못된 타입으로 생성한다")
    public void 관리자가상품을잘못된타입으로생성한다(String name) {
        Map<String, Object> requestBody = Map.of(
                "name", name + "-" + System.currentTimeMillis(),
                "price", 100000,
                "stockQuantity", 5,
                "productType", "INVALID"
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/products", requestBody);
    }

    @When("관리자가 {string} 상품을 음수 가격으로 생성한다")
    public void 관리자가상품을음수가격으로생성한다(String name) {
        Map<String, Object> requestBody = Map.of(
                "name", name,
                "price", -1000,
                "stockQuantity", 10,
                "productType", "SALE"
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/products", requestBody);
    }

    @When("관리자가 {string} 상품을 음수 재고로 생성한다")
    public void 관리자가상품을음수재고로생성한다(String name) {
        Map<String, Object> requestBody = Map.of(
                "name", name,
                "price", 10000,
                "stockQuantity", -5,
                "productType", "SALE"
        );
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/products", requestBody);
    }

    @When("권한 없는 사용자가 {string} 상품을 생성한다")
    public void 권한없는사용자가상품을생성한다(String name) {
        Map<String, Object> requestBody = Map.of(
                "name", name,
                "price", 10000,
                "stockQuantity", 10,
                "productType", "SALE"
        );
        ApiHelper.makeUnauthenticatedRequest("POST", "/admin/products", requestBody);
    }
}
