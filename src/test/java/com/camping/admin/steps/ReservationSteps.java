package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {
    private int reservationId;

    @When("관리자가 로그인을 한다.")
    public void 관리자가로그인을한다() {
        RestAssured.given()
                .when()
                .post("http://localhost:8080/admin/login")
                .then()
                .statusCode(200);
    }

    @When("관리자가 예약 상태를 변경한다.")
    public void 관리자가예약상태를변경한다() {
        reservationId = 1;
        RestAssured.given()
                .when()
                .patch("http://localhost:8080/admin/reservations/" + reservationId + "/status")
                .then()
                .statusCode(200);
    }

    @Then("예약 상태가 변경된다.")
    public void 예약상태가변경된다() {
        ExtractableResponse<Response> response = RestAssured.given()
                .when()
                .get("http://localhost:8080/admin/reservations")
                .then()
                .statusCode(200)
                .extract();

        String status = response.jsonPath().getList("$").stream()
                .filter(s -> ((Integer) ((Map<String, Object>) s).get("id")) == 1)
                .findFirst()
                .toString();

        assertThat(status).contains("CONFIRMED");
    }
}


//[
//        {
//        "id": 1,
//        "customerName": "�솉湲몃룞",
//        "startDate": "2025-09-10",
//        "endDate": "2025-09-11",
//        "status": "CONFIRMED",
//        "campsiteSiteNumber": "A-01",
//        "reservationDate": "2025-09-10"
//        },
//]