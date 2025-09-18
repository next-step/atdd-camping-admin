package com.camping.admin.steps;

import com.camping.admin.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReservationSteps {

    @Given("{string} 상태인 예약이 있다")
    public void stateReservation(String status) {
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
    public void changeReservation(String newStatus) {
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
    public void completeReservation(String expectedStatus) {
        Response lastResponse = ScenarioContext.get().get("response", Response.class);
        Long id = ScenarioContext.get().get("reservationId", Long.class);

        lastResponse.then()
                .statusCode(200)
                .body("status", Matchers.equalTo(expectedStatus))
                .body("id", Matchers.equalTo(id.intValue()));
    }

    @When("예약 상태 변경을 빈 본문으로 요청한다")
    public void changeStatusWithEmptyBody() {
        String token = ScenarioContext.get().get("accessToken", String.class);
        Long reservationId = ScenarioContext.get().get("reservationId", Long.class);

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of())
                .when()
                .patch("/admin/reservations/" + reservationId + "/status");

        ScenarioContext.get().set("response", response);
    }

    @Given("아무 예약이 있다")
    public void anyReservationExists() {
        String token = ScenarioContext.get().get("accessToken", String.class);

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> reservations = response.jsonPath().getList("");
        Long reservationId = Long.valueOf(reservations.get(0).get("id").toString());
        ScenarioContext.get().set("reservationId", reservationId);
    }

    @When("예약을 공백 상태로 변경한다")
    public void changeReservationToBlank() {
        String token = ScenarioContext.get().get("accessToken", String.class);
        Long reservationId = ScenarioContext.get().get("reservationId", Long.class);

        // 현재 상태 조회 후 저장
        Response listResponse = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> reservations = listResponse.jsonPath().getList("");
        Optional<String> currentStatus = reservations.stream()
                .filter(r -> Long.valueOf(r.get("id").toString()).equals(reservationId))
                .map(r -> r.get("status").toString())
                .findFirst();
        currentStatus.ifPresent(s -> ScenarioContext.get().set("previousStatus", s));

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("status", ""))
                .when()
                .patch("/admin/reservations/" + reservationId + "/status");

        ScenarioContext.get().set("response", response);
    }

    @Then("예약 상태가 기존 상태로 유지된다")
    public void reservationStatusUnchanged() {
        Response lastResponse = ScenarioContext.get().get("response", Response.class);
        String previous = ScenarioContext.get().get("previousStatus", String.class);
        lastResponse.then()
                .statusCode(200)
                .body("status", Matchers.equalTo(previous));
    }

    @Given("존재하지 않는 예약 ID를 사용한다")
    public void useNonExistingReservationId() {
        ScenarioContext.get().set("reservationId", 999999L);
    }

    @Then("응답 상태 코드는 {int}다")
    public void responseStatusIs(int expected) {
        Response lastResponse = ScenarioContext.get().get("response", Response.class);
        lastResponse.then().statusCode(expected);
    }

    @When("예약 상태 변경 정보를 제공하지 않고 요청한다")
    public void requestStatusChangeWithoutInfo() {
        String token = ScenarioContext.get().get("accessToken", String.class);
        Long reservationId = ScenarioContext.get().get("reservationId", Long.class);

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .patch("/admin/reservations/" + reservationId + "/status");

        ScenarioContext.get().set("response", response);
    }

    @Then("상태 변경이 실패한다")
    public void statusChangeFails() {
        Response lastResponse = ScenarioContext.get().get("response", Response.class);
        lastResponse.then().statusCode(Matchers.greaterThanOrEqualTo(400));
    }

    @When("예약을 내용을 상태로 변경한다")
    public void changeReservationToEmptyStatus() {
        String token = ScenarioContext.get().get("accessToken", String.class);
        Long reservationId = ScenarioContext.get().get("reservationId", Long.class);

        // 현재 상태 조회 후 저장
        Response listResponse = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> reservations = listResponse.jsonPath().getList("");
        Optional<String> currentStatus = reservations.stream()
                .filter(r -> Long.valueOf(r.get("id").toString()).equals(reservationId))
                .map(r -> r.get("status").toString())
                .findFirst();
        currentStatus.ifPresent(s -> ScenarioContext.get().set("previousStatus", s));

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("status", ""))
                .when()
                .patch("/admin/reservations/" + reservationId + "/status");

        ScenarioContext.get().set("response", response);
    }

    @Then("예약 상태가 {string}으로 유지된다")
    public void reservationStatusMaintained(String expectedStatus) {
        Response lastResponse = ScenarioContext.get().get("response", Response.class);
        lastResponse.then()
                .statusCode(200)
                .body("status", Matchers.equalTo(expectedStatus));
    }

    @Given("존재하지 않는 예약 번호를 사용한다")
    public void useNonExistingReservationNumber() {
        ScenarioContext.get().set("reservationId", 999999L);
    }

    @Then("{string} 오류가 발생한다")
    public void errorOccurs(String expectedError) {
        Response lastResponse = ScenarioContext.get().get("response", Response.class);
        lastResponse.then()
                .statusCode(Matchers.greaterThanOrEqualTo(400))
                .body("message", Matchers.containsString(expectedError));
    }
}

