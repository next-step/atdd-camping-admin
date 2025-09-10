package com.camping.admin.steps;

import com.camping.admin.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.util.Map;

public class ReservationSteps {

    @Given("{string} 상태인 예약이 있다")
    public void 상태인_예약이_있다(String status) {
        String token = ScenarioContext.get().get("accessToken", String.class);

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract().response();

        Long reservationId = response.jsonPath()
                .getList("", Map.class)
                .stream()
                .filter(reservation -> status.equals(reservation.get("status")))
                .map(reservation -> Long.valueOf(reservation.get("id").toString()))
                .findFirst()
                .orElseThrow();

        ScenarioContext.get().set("reservationId", reservationId);
    }

    @When("예약을 {string} 상태로 변경한다")
    public void 예약을_상태로_변경한다(String newStatus) {
        String token = ScenarioContext.get().get("accessToken", String.class);
        Long reservationId = ScenarioContext.get().get("reservationId", Long.class);

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("status", newStatus))
                .when()
                .patch("/admin/reservations/" + reservationId + "/status");

        ScenarioContext.get().set("response", response);
    }

    @Then("예약 상태가 {string}으로 변경된다")
    public void 예약_상태가_으로_변경된다(String expectedStatus) {
        Response lastResponse = ScenarioContext.get().get("response", Response.class);
        Long id = ScenarioContext.get().get("reservationId", Long.class);

        lastResponse.then()
                .statusCode(200)
                .body("status", Matchers.equalTo(expectedStatus))
                .body("id", Matchers.equalTo(id.intValue()));
    }
}

