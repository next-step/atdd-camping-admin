package com.camping.admin.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReservationSteps {

    private String accessToken;
    private Long targetReservationId;
    private Response lastResponse;

    @Given("관리자가 로그인했다")
    public void 관리자가_로그인했다() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().response();
        
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Given("{string} 상태인 예약이 있다")
    public void 상태인_예약이_있다(String status) {
        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract().response();

        targetReservationId = response.jsonPath()
                .getList("", Map.class)
                .stream()
                .filter(reservation -> status.equals(reservation.get("status")))
                .map(reservation -> Long.valueOf(reservation.get("id").toString()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("상태가 '" + status + "'인 예약을 찾을 수 없습니다"));
    }

    @When("예약을 {string} 상태로 변경한다")
    public void 예약을_상태로_변경한다(String newStatus) {
        lastResponse = RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(Map.of("status", newStatus))
                .when()
                .patch("/admin/reservations/" + targetReservationId + "/status");
    }

    @Then("예약 상태가 {string}으로 변경된다")
    public void 예약_상태가_으로_변경된다(String expectedStatus) {
        lastResponse
                .then()
                .statusCode(200)
                .body("status", Matchers.equalTo(expectedStatus))
                .body("id", Matchers.equalTo(targetReservationId.intValue()));

        RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .body("find { it.id == " + targetReservationId + " }.status", Matchers.equalTo(expectedStatus));
    }
}

