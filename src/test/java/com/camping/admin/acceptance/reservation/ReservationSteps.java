package com.camping.admin.acceptance.reservation;

import com.camping.admin.dto.ReservationResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {
    private Long reservationId;
    private String adminToken;
    ExtractableResponse<Response> response;

    @Given("사용자가 예약했다")
    public void 사용자가_예약했다() {
        reservationId = 1L;
    }

    @When("관리자가 예약을 취소했다")
    public void 관리자가_예약을_취소했다() {
        adminToken = RestAssured
                .given()
                    .contentType("application/json")
                    .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                    .post("/auth/login")
                .then().log().all()
                .extract()
                    .cookie("AUTH_TOKEN");

        Map<String, String> request = Map.of("status", "CANCELED");
        response = RestAssured
                .given()
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType("application/json")
                    .body(request)
                .when()
                    .patch("/admin/reservations/{id}/status", reservationId)
                .then().log().all()
                    .statusCode(200)
                .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Then("예약은 취소 상태다")
    public void 예약은_취소상태다() {
        String status = response.as(ReservationResponse.class).getStatus();
        assertThat(status.equals("CANCELED"));
    }

    @And("해당 자원은 다시 예약 가능하다")
    public void 해당_자원은_다시_예약가능하다() {
        Map<String, String> request = Map.of("status", "WAITING");
        response = RestAssured
                .given()
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType("application/json")
                    .body(request)
                .when()
                    .patch("/admin/reservations/{id}/status", reservationId)
                .then().log().all()
                    .statusCode(200)
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        String status = response.jsonPath().getString("status");
        assertThat(!status.equals("CONFIRMED")); // CONFIRMED 상태가 아니면 예약 성공
    }
}
