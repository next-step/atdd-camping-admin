package com.camping.admin.steps;

import com.camping.admin.context.ScenarioContext;
import com.camping.admin.utils.AcceptanceTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.util.List;
import java.util.Map;

public class ReservationSteps extends AcceptanceTest {

    private String getAccessToken() {
        return ScenarioContext.get().get("accessToken", String.class);
    }

    private Long getReservationId() {
        return ScenarioContext.get().get("reservationId", Long.class);
    }

    private Response getReservationsList() {
        String token = getAccessToken();
        return RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract().response();
    }

    private List<Map<String, Object>> getReservationsFromResponse(Response response) {
        return response.jsonPath().getList("");
    }

    private void setReservationId(Long reservationId) {
        ScenarioContext.get().set("reservationId", reservationId);
    }

    private void setResponse(Response response) {
        ScenarioContext.get().set("response", response);
    }

    private Response getLastResponse() {
        return ScenarioContext.get().get("response", Response.class);
    }

    private Response patchReservationStatus(Long reservationId, String status) {
        String token = getAccessToken();
        return RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("status", status))
                .when()
                .patch("/admin/reservations/" + reservationId + "/status");
    }

    private Response patchReservationStatusWithoutBody(Long reservationId) {
        String token = getAccessToken();
        return RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .patch("/admin/reservations/" + reservationId + "/status");
    }

    @Given("{string} 상태인 예약이 있다")
    public void stateReservation(String status) {
        Response response = getReservationsList();
        List<Map<String, Object>> reservations = getReservationsFromResponse(response);

        Long reservationId = reservations.stream()
                .filter(reservation -> status.equals(reservation.get("status")))
                .map(reservation -> Long.valueOf(reservation.get("id").toString()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No reservation found with status: " + status));

        setReservationId(reservationId);
    }

    @When("예약을 {string} 상태로 변경한다")
    public void changeReservation(String newStatus) {
        Long reservationId = getReservationId();
        Response response = patchReservationStatus(reservationId, newStatus);
        setResponse(response);
    }

    @Then("예약 상태가 {string}으로 변경된다")
    public void completeReservation(String expectedStatus) {
        Response lastResponse = getLastResponse();
        Long id = getReservationId();

        lastResponse.then()
                .statusCode(200)
                .body("status", Matchers.equalTo(expectedStatus))
                .body("id", Matchers.equalTo(id.intValue()));
    }


    @Given("존재하지 않는 예약 번호를 사용한다")
    public void useNonExistingReservationId() {
        setReservationId(999999L);
    }

    @When("예약 상태 변경 정보를 제공하지 않고 요청한다")
    public void requestStatusChangeWithoutInfo() {
        Long reservationId = getReservationId();
        Response response = patchReservationStatusWithoutBody(reservationId);
        setResponse(response);
    }

    @Then("상태 변경이 실패한다")
    public void statusChangeFails() {
        Response lastResponse = getLastResponse();
        lastResponse.then().statusCode(Matchers.greaterThanOrEqualTo(400));
    }

}

