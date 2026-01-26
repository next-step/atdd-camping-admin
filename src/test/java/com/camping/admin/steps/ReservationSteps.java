package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ReservationSteps {

    private TestContext context = TestContext.getInstance();
    private int reservationId;

    // Given

    @Given("고객이 예약한 예약건 {int}이 있다")
    public void 고객이_예약한_예약건이_있다(int id) {
        this.reservationId = id;
    }

    // When

    @When("관리자가 예약건 {int}을 취소한다")
    public void 관리자가_예약건을_취소한다(int id) {
        context.setLastResponse(changeReservationStatus(id, "CANCELLED"));
    }

    @When("관리자가 예약건 {int}을 확정한다")
    public void 관리자가_예약건을_확정한다(int id) {
        context.setLastResponse(changeReservationStatus(id, "CONFIRMED"));
    }

    @When("관리자가 존재하지 않는 예약 {int}을 취소한다")
    public void 관리자가_존재하지_않는_예약을_취소한다(int id) {
        context.setLastResponse(changeReservationStatus(id, "CANCELLED"));
    }

    @When("관리자가 존재하지 않는 예약 {int}을 확정한다")
    public void 관리자가_존재하지_않는_예약을_확정한다(int id) {
        context.setLastResponse(changeReservationStatus(id, "CONFIRMED"));
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
                .spec(context.getAuthHelper().authorizedSpec())
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .body("find { it.id == " + reservationId + " }.status", equalTo("CANCELLED"));
    }


    // helper

    private Response changeReservationStatus(int id, String status) {
        return given()
                .spec(context.getAuthHelper().authorizedSpec())
                .body(Map.of("status", status))
                .when()
                .patch("/admin/reservations/" + id + "/status")
                .then()
                .extract().response();
    }

    private void assertReservationStatus(int statusCode, String expectedStatus) {
        context.getLastResponse().then()
                .statusCode(statusCode)
                .body("status", equalTo(expectedStatus));
    }
}
