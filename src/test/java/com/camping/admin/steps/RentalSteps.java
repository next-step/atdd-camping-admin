package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class RentalSteps {

    @Autowired
    private TestContext testContext;

    @When("관리자가 {string} 고객의 예약에 {string} 상품을 대여 처리하면")
    public void 관리자가_대여_처리하면(String customerName, String productName) {
        var authToken = testContext.getAuthToken();
        var reservation = testContext.getReservation(customerName);
        var product = testContext.getProduct(productName);

        Map<String, Object> rentalRequest = Map.of(
            "reservationId", reservation.getId(),
            "productId", product.getId(),
            "quantity", 1
        );

        var response = RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .contentType(ContentType.JSON)
            .body(rentalRequest)
            .when()
            .post("/admin/rentals")
            .then()
            .extract();

        testContext.setResponse(response);
    }

    @Then("대여 처리가 성공한다")
    public void 대여_처리가_성공한다() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @And("대여된 상품의 이름은 {string}이다")
    public void 대여된_상품의_이름은(String productName) {
        var response = testContext.getResponse();
        String actualProductName = response.jsonPath().getString("productName");
        assertThat(actualProductName).isEqualTo(productName);
    }
}
