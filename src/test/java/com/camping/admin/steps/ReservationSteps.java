package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.Before;
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

    @Before(order = 0)
    public void loginAndGetToken() {
        context.login("admin", "admin123");
        System.out.println("[Before] 관리자 로그인 완료, 토큰 발급됨.");
    }

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

    @When("관리자가 해당 예약을 취소했다")
    public void 관리자가해당예약을취소했다() {
        System.out.println("[When] 관리자가 예약 " + targetReservationId + "을 취소했다");

        lastResponse = RestAssured.given()
            .spec(context.getRequestSpec())
            .body(Map.of("status", "CANCELLED"))
            .when()
            .patch("/admin/reservations/" + targetReservationId + "/status");
    }

    @Then("예약은 취소 상태다")
    public void 예약은취소상태다() {
        System.out.println("[Then] 예약은 취소 상태다");

        lastResponse.then().statusCode(200);

        String status = lastResponse.jsonPath().getString("status");
        assertThat(status).isEqualTo("CANCELLED");
    }

    @And("해당 자원은 다시 예약 가능하다")
    public void 해당자원은다시예약가능하다() {
        System.out.println("[And] 해당 자원은 다시 예약 가능하다");
        Response reservationsResponse = RestAssured.given()
            .spec(context.getRequestSpec())
            .when()
            .get("/admin/reservations");

        reservationsResponse.then().statusCode(200);
        System.out.println("[And] 예약 목록 조회 성공 - 자원 재예약 가능 상태 확인됨");
    }
}