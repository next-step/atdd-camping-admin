package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class RentalStepDefs {
    private Response lastResponse;
    private Long reservationId;
    private Long productId;
    private Map<String, Object> rentalData;

    @Given("확정된 예약 {int}이 존재한다")
    public void 확정된예약이존재한다(int id) {
        reservationId = (long) id; // data.sql의 기존 예약 데이터 활용
    }

    @Given("대여 가능한 상품 {int}이 존재한다")
    public void 대여가능한상품이존재한다(int id) {
        productId = (long) id; // data.sql의 기존 RENTAL 상품 활용 (랜턴)
    }

    @When("관리자가 예약 {int}에 상품 {int}을 {int}개 대여 등록한다")
    public void 관리자가예약에대여를등록한다(int reservationId, int productId, int quantity) {
        this.reservationId = (long) reservationId;
        this.productId = (long) productId;
        
        rentalData = Map.of(
                "reservationId", reservationId,
                "productId", productId,
                "quantity", quantity
        );
        
        lastResponse = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(rentalData)
                .post("/admin/rentals");
    }

    @When("관리자가 상품 {int}을 {int}개 워크인 대여로 등록한다")
    public void 관리자가워크인대여를등록한다(int productId, int quantity) {
        this.productId = (long) productId;
        
        rentalData = Map.of(
                "productId", productId,
                "quantity", quantity
        );
        
        lastResponse = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(rentalData)
                .post("/admin/rentals");
    }

    @Then("대여 생성이 성공한다")
    public void 대여생성이성공한다() {
        lastResponse.then().statusCode(201);
    }

    @And("대여 정보가 올바르게 저장된다")
    public void 대여정보가올바르게저장된다() {
        lastResponse.then()
                .body("productId", equalTo(productId.intValue()))
                .body("quantity", equalTo(rentalData.get("quantity")))
                .body("id", notNullValue())
                .body("isReturned", equalTo(false));
        
        if (rentalData.containsKey("reservationId")) {
            lastResponse.then().body("reservationId", equalTo(reservationId.intValue()));
        }
    }
}
