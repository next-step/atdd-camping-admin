package com.camping.admin.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ReservationSteps {

    private String adminToken;
    private Response lastResponse;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        loginAsAdmin();
    }

    private void loginAsAdmin() {
        adminToken = given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .cookie("AUTH_TOKEN");
    }

    @Given("고객이 캠핑장을 예약했다")
    public void 고객이_캠핑장을_예약했다() {

    }

    @When("관리자가 예약 {int}을 취소한다")
    public void 관리자가_예약을_취소한다(int reservationId) {
        lastResponse = given()
                .contentType("application/json")
                .accept("application/json")
                .cookie("AUTH_TOKEN", adminToken)
                .body(Map.of("status", "CANCELLED"))
                .when()
                .patch("/admin/reservations/" + reservationId + "/status")
                .then()
                .extract().response();
    }

    @Then("예약 취소가 완료된다")
    public void 예약_취소가_완료된다() {
        lastResponse.then()
                .statusCode(200)
                .body("status", equalTo("CANCELLED"));
    }

    @Then("해당 캠핑장은 다시 예약 가능하다")
    public void 해당_캠핑장은_다시_예약_가능하다() {
        given()
                .contentType("application/json")
                .accept("application/json")
                .cookie("AUTH_TOKEN", adminToken)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .body("[0].status", equalTo("CANCELLED"));
    }
}
