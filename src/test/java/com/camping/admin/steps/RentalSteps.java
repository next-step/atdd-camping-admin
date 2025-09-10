package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static com.camping.admin.steps.TestFixture.대여기록목록조회;
import static org.assertj.core.api.Assertions.assertThat;

public class RentalSteps {
    private int rentalId;

    @When("관리자는 특정 상품의 수량 만큼 대여 기록을 작성한다.")
    public void 관리자는특정상품의수량만큼대여기록을작성한다() {
        ExtractableResponse<Response> response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + StepContext.getAccessToken())
                .body("{\"reservationId\": 1, \"productId\": 1, \"quantity\": 2}")
                .when()
                .post("http://localhost:8081/admin/rentals")
                .then()
                .statusCode(201)
                .extract();

        rentalId = response.jsonPath().getInt("id");
    }

    @Then("대여 기록이 생성된다.")
    public void 대여기록이생성된다() {
        Map<String, Object> rental = 대여기록목록조회(rentalId);
        assertThat(rental.get("isReturned")).isEqualTo(false);
    }

    @And("상품의 수량이 대여 기록의 수량 만큼 줄어든다.")
    public void 상품의수량이대여기록의수량만큼줄어든다() {
        Map<String, Object> rental = 대여기록목록조회(rentalId);
        assertThat(rental.get("quantity")).isEqualTo(2);
    }
}