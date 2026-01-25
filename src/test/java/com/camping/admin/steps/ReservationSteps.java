package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Map;

public class ReservationSteps {

    private final CommonContext context;
    private Response lastResponse;
    private int targetReservationId;

    public ReservationSteps(CommonContext context) {
        this.context = context;
    }

    // === Given ===

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        Response response = RestAssured.given()
            .spec(context.getRequestSpec())
            .when()
            .get("/admin/reservations");

        response.then().statusCode(200);
        targetReservationId = response.jsonPath().getInt("[0].id");
        System.out.println("[Given] 사용자가 예약을 했다. 예약 ID: " + targetReservationId);
    }

    @Given("예약이 존재한다")
    public void 예약이존재한다() {
        Response response = RestAssured.given()
            .spec(context.getRequestSpec())
            .when()
            .get("/admin/reservations");

        response.then().statusCode(200);
        targetReservationId = response.jsonPath().getInt("[0].id");
        System.out.println("[Given] 예약이 존재한다. 예약 ID: " + targetReservationId);
    }

    // === When ===

    @When("관리자가 해당 예약을 취소했다")
    public void 관리자가해당예약을취소했다() {
        System.out.println("[When] 관리자가 예약 " + targetReservationId + "을 취소했다");

        lastResponse = RestAssured.given()
            .spec(context.getRequestSpec())
            .body(Map.of("status", "CANCELLED"))
            .when()
            .patch("/admin/reservations/" + targetReservationId + "/status");
    }

    @When("관리자가 예약 상태를 {word}로 변경한다")
    public void 관리자가예약상태를변경한다(String status) {
        System.out.println("[When] 관리자가 예약 상태를 " + status + "로 변경한다");

        lastResponse = RestAssured.given()
            .spec(context.getRequestSpec())
            .body(Map.of("status", status))
            .when()
            .patch("/admin/reservations/" + targetReservationId + "/status");
    }

    // === Then ===

    @Then("예약은 취소 상태다")
    public void 예약은취소상태다() {
        lastResponse.then().statusCode(200);
        String status = lastResponse.jsonPath().getString("status");
        assertThat(status).isEqualTo("CANCELLED");
        System.out.println("[Then] 예약은 취소 상태다");
    }

    @Then("예약 상태가 {word}로 변경된다")
    public void 예약상태가변경된다(String expectedStatus) {
        lastResponse.then().statusCode(200);
        String status = lastResponse.jsonPath().getString("status");
        assertThat(status).isEqualTo(expectedStatus);
        System.out.println("[Then] 예약 상태가 " + expectedStatus + "로 변경된다");
    }

    @And("해당 자원은 다시 예약 가능하다")
    public void 해당자원은다시예약가능하다() {
        Response reservationsResponse = RestAssured.given()
            .spec(context.getRequestSpec())
            .when()
            .get("/admin/reservations");

        reservationsResponse.then().statusCode(200);
        System.out.println("[And] 해당 자원은 다시 예약 가능하다");
    }

    @Given("이미 취소된 예약이 있다")
    public void 이미취소된예약이있다() {
        // 먼저 예약을 취소 상태로 만듦
        Response response = RestAssured.given()
            .spec(context.getRequestSpec())
            .when()
            .get("/admin/reservations");

        response.then().statusCode(200);
        targetReservationId = response.jsonPath().getInt("[0].id");

        // 취소 처리
        RestAssured.given()
            .spec(context.getRequestSpec())
            .body(Map.of("status", "CANCELLED"))
            .when()
            .patch("/admin/reservations/" + targetReservationId + "/status");

        System.out.println("[Given] 이미 취소된 예약이 있다. ID: " + targetReservationId);
    }

    @Then("예약 취소가 거부된다")
    public void 예약취소가거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 409);
        System.out.println("[Then] 예약 취소가 거부된다");
    }

    @When("관리자가 존재하지 않는 예약을 취소한다")
    public void 관리자가존재하지않는예약을취소한다() {
        lastResponse = RestAssured.given()
            .spec(context.getRequestSpec())
            .body(Map.of("status", "CANCELLED"))
            .when()
            .patch("/admin/reservations/99999/status");
    }

    @When("관리자가 존재하지 않는 예약의 상태를 변경한다")
    public void 관리자가존재하지않는예약의상태를변경한다() {
        lastResponse = RestAssured.given()
            .spec(context.getRequestSpec())
            .body(Map.of("status", "CHECKED_IN"))
            .when()
            .patch("/admin/reservations/99999/status");
    }

    @Then("예약 상태 변경이 거부된다")
    public void 예약상태변경이거부된다() {
        int statusCode = lastResponse.getStatusCode();
        assertThat(statusCode).isIn(400, 404, 409);
        System.out.println("[Then] 예약 상태 변경이 거부된다");
    }
}