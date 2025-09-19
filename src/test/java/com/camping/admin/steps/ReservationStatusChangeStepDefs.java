package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@SuppressWarnings("NonAsciiCharacters")
public class ReservationStatusChangeStepDefs {

    Long reservationId = 1L;

    @Given("고객이 예약을 확정한 상태에서")
    public void 고객이_예약을_확정한_상태에서() {
        String adminToken = CommonContext.getAdminToken();

        ExtractableResponse<Response> response = RestAssured
            .given()
                .header("Authorization", "Bearer " + adminToken)
            .when()
                .get("/admin/reservations")
            .then()
                .log().all()
            .statusCode(OK.value())
                .extract();

        List<Map<String, Object>> reservations = response.jsonPath().getList("$");

        assertThat(reservations)
            .filteredOn(r -> ((Number) r.get("id")).longValue() == reservationId)
            .extracting(r -> (String) r.get("status"))
            .containsExactly("CONFIRMED");
    }

    @When("고객이 예약 취소를 요청하여 관리자가 해당 예약을 취소 처리하면")
    public void 고객이_예약_취소를_요청하여_관리자가_해당_예약을_취소_처리하면() {
        String adminToken = CommonContext.getAdminToken();

        ExtractableResponse<Response> response = RestAssured
            .given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
            .when()
                .body(Map.of("status", "CANCELLED"))
                .patch("/admin/reservations/{reservationId}/status", reservationId)
            .then()
                .log().all()
                .statusCode(OK.value())
                .extract();

        Map<String, Object> updatedReservation = response.jsonPath().get();

        assertThat(updatedReservation.get("status")).isEqualTo("CANCELLED");

    }

    @Then("고객의 예약이 취소 상태로 변경된다")
    public void 고객의_예약이_취소_상태로_변경된다() {
        String adminToken = CommonContext.getAdminToken();

        ExtractableResponse<Response> response = RestAssured
            .given()
                .header("Authorization", "Bearer " + adminToken)
            .when()
                .get("/admin/reservations")
            .then()
                .log().all()
                .statusCode(OK.value())
                .extract();

        List<Map<String, Object>> reservations = response.jsonPath().getList("$");

        assertThat(reservations)
            .filteredOn(r -> ((Number) r.get("id")).longValue() == reservationId)
            .extracting(r -> (String) r.get("status"))
            .containsExactly("CANCELLED");
    }
}
