package com.camping.admin.steps;

import com.camping.admin.support.AuthHelper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ReservationSteps {

    private AuthHelper authHelper;
    private int reservationId;
    private Response lastResponse;

    @Before
    public void setup() {
        authHelper = new AuthHelper();
    }

    // Given

    @Given("고객이 예약한 예약건 {int}이 있다")
    public void 고객이_예약한_예약건이_있다(int id) {
        this.reservationId = id;
    }

    @Given("존재하지 않는 예약건 {int}이 있다")
    public void 존재하지_않는_예약건이_있다(int id) {
        this.reservationId = id;
    }

    // When

    @When("관리자가 예약건 {int}을 취소한다")
    public void 관리자가_예약건을_취소한다(int id) {
        lastResponse = changeReservationStatus(id, "CANCELLED");
    }

    @When("관리자가 예약건 {int}을 확정한다")
    public void 관리자가_예약건을_확정한다(int id) {
        lastResponse = changeReservationStatus(id, "CONFIRMED");
    }

    // Then

    @Then("예약 취소가 완료된다")
    public void 예약_취소가_완료된다() {
        assertReservationStatus(200, "CANCELLED");
    }

    @Then("예약 확정이 완료된다")
    public void 예약_확정이_완료된다() {
        assertReservationStatus(200, "CONFIRMED");
    }

    @Then("해당 캠핑장은 다시 예약 가능하다")
    public void 해당_캠핑장은_다시_예약_가능하다() {
        given()
                .spec(authHelper.authorizedSpec())
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .body("find { it.id == " + reservationId + " }.status", equalTo("CANCELLED"));
    }

    @Then("에러가 발생한다")
    public void 에러가_발생한다() {
        lastResponse.then().statusCode(500);
    }

    // helper

    private Response changeReservationStatus(int id, String status) {
        return given()
                .spec(authHelper.authorizedSpec())
                .body(Map.of("status", status))
                .when()
                .patch("/admin/reservations/" + id + "/status")
                .then()
                .extract().response();
    }

    private void assertReservationStatus(int statusCode, String expectedStatus) {
        lastResponse.then()
                .statusCode(statusCode)
                .body("status", equalTo(expectedStatus));
    }
}
