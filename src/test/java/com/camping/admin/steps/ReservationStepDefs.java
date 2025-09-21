package com.camping.admin.steps;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.dto.ReservationResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class ReservationStepDefs {

    private Long reservationId;
    private String adminToken;
    private String status;
    private ExtractableResponse response;

    @Given("WATING 상태인 예약이 존재한다.")
    public void wating상태인예약이존재한다() {
        reservationId = 13L;
    }

    @When("관리자가 WATING 상태인 예약 상태를 PENDING 상태로 변경한다.")
    public void 관리자가WATING상태인예약상태를PENDING상태로변경한다() {
        adminToken = given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when().post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");

        var body = Map.of("status", "PENDING");
        var response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(body)
                .when()
                .patch("/admin/reservations/{id}/status", reservationId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        ReservationResponse reservationResponse = response.as(ReservationResponse.class);
        status = reservationResponse.getStatus();
    }

    @Then("예약 상태가 PENDING 으로 변경된다.")
    public void 예약상태가PENDING으로변경된다() {
        assertThat(status).isEqualTo("PENDING");
    }

    @Given("존재하지 않는 예약 ID 9999 가 있다.")
    public void 존재하지않는예약ID가있다() {
        reservationId = 9999L;
    }

    @When("관리자가 예약 ID 9999 의 상태를 PENDING 상태로 변경한다.")
    public void 관리자가예약ID의상태를PENDING상태로변경한다() {
        adminToken = given()
                .contentType("application/json")
                .body(Map.of("username", "admin", "password", "admin123"))
                .when().post("/auth/login")
                .then().log().all()
                .extract()
                .cookie("AUTH_TOKEN");

        var body = Map.of("status", "PENDING");

        response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(body)
                .when()
                .patch("/admin/reservations/{id}/status", reservationId)
                .then().log().all()
                .extract();
    }

    @Then("에러 응답이 발생한다.")
    public void 에러응답이발생한다() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
