package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RentalStepDefs {

    @When("관리자가 대여 목록을 조회한다")
    public void 관리자가대여목록을조회한다() {
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .get("/admin/rentals");
        CommonContext.setLastResponse(response);
    }

    @When("관리자가 다음 정보로 대여를 생성한다:")
    public void 관리자가다음정보로대여를생성한다(DataTable dataTable) {
        Map<String, String> rentalData = dataTable.asMaps().get(0);
        
        Map<String, Object> requestBody = new java.util.HashMap<>();
        
        if (rentalData.containsKey("productId")) {
            requestBody.put("productId", Long.parseLong(rentalData.get("productId")));
        }
        if (rentalData.containsKey("quantity")) {
            requestBody.put("quantity", Integer.parseInt(rentalData.get("quantity")));
        }
        if (rentalData.containsKey("reservationId")) {
            requestBody.put("reservationId", Long.parseLong(rentalData.get("reservationId")));
        }
        
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(requestBody)
                .post("/admin/rentals");
        CommonContext.setLastResponse(response);
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
}
