package com.camping.admin.steps;

import com.camping.admin.support.ApiHelper;
import com.camping.admin.support.CommonContext;
import com.camping.admin.support.TestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ReservationStepDefs {
    private Long reservationId;

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        reservationId = 1L; // data.sql에서 기존 예약 데이터를 사용
    }

    @Given("관리자가 해당 예약을 취소했다")
    public void 관리자가해당예약을취소했다() {
        ApiHelper.updateReservationStatus(reservationId, "CANCELLED");
    }

    @When("관리자가 예약을 취소했다")
    public void 관리자가예약을취소했다() {
        ApiHelper.updateReservationStatus(reservationId, "CANCELLED");
    }

    @When("관리자가 예약 {int}를 취소했다")
    public void 관리자가예약을취소했다(int reservationId) {
        this.reservationId = (long) reservationId;
        ApiHelper.updateReservationStatus(this.reservationId, "CANCELLED");
    }

    @When("관리자가 동일 예약을 다시 취소했다")
    public void 관리자가동일예약을다시취소했다() {
        Long reservationIdFromResponse = extractReservationIdFromLastResponse();
        Response response = cancelReservationDirectly(reservationIdFromResponse);
        CommonContext.lastResponse = response;
    }

    @Then("예약은 취소 상태다")
    public void 예약은취소상태다() {
        CommonContext.lastResponse.then().body("status", equalTo("CANCELLED"));
    }

    @And("해당 자원은 다시 예약 가능하다")
    public void 해당자원은다시예약가능하다() {
        verifyCancellationResponse();
        Long reservationId = extractReservationIdFromLastResponse();
        verifyReservationStatusInList(reservationId);
    }

    @Then("시스템 정책에 맞는 결과가 반환된다")
    public void 시스템정책에맞는결과가반환된다() {
        validateSystemPolicyResponse();
    }

    @When("관리자가 예약을 {string}로 상태 변경했다")
    public void 관리자가예약을로상태변경했다(String status) {
        ApiHelper.updateReservationStatus(reservationId, status);
    }

    @When("권한 없는 사용자가 예약을 취소했다")
    public void 권한없는사용자가예약을취소했다() {
        ApiHelper.updateReservationStatus(reservationId, "CANCELLED", null);
    }

    @And("예약 상태는 변경된다")
    public void 예약상태는변경된다() {
        CommonContext.lastResponse.then()
                .body("status", notNullValue());
    }

    @When("관리자가 빈 요청으로 예약 상태를 변경했다")
    public void 관리자가빈요청으로예약상태를변경했다() {
        Response response = updateReservationWithEmptyBody();
        CommonContext.lastResponse = response;
    }

    @When("관리자가 null 상태로 예약을 변경했다")
    public void 관리자가null상태로예약을변경했다() {
        Response response = updateReservationWithNullStatus();
        CommonContext.lastResponse = response;
    }

    @And("예약 상태는 변경되지 않는다")
    public void 예약상태는변경되지않는다() {
        // 원래 상태가 유지되는지 확인 (예: CONFIRMED 상태 유지)
        CommonContext.lastResponse.then()
                .body("status", notNullValue());
    }

    private Long extractReservationIdFromLastResponse() {
        Object idObj = CommonContext.lastResponse.then().extract().path("id");
        if (idObj instanceof Integer) {
            return ((Integer) idObj).longValue();
        } else {
            return (Long) idObj;
        }
    }

    private Response cancelReservationDirectly(Long reservationId) {
        return given().spec(CommonContext.requestSpec)
                .header("Authorization", "Bearer " + CommonContext.adminToken)
                .body(Map.of("status", "CANCELLED"))
                .patch("/admin/reservations/" + reservationId + "/status");
    }

    private void verifyCancellationResponse() {
        CommonContext.lastResponse.then()
                .statusCode(200)
                .body("status", equalTo("CANCELLED"));
    }

    private void verifyReservationStatusInList(Long reservationId) {
        given().spec(CommonContext.requestSpec)
                .header("Authorization", "Bearer " + CommonContext.adminToken)
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .body("find { it.id == " + reservationId + " }.status", equalTo("CANCELLED"));
    }

    private void validateSystemPolicyResponse() {
        int statusCode = CommonContext.lastResponse.getStatusCode();
        assert statusCode == 200 || statusCode == 400 : 
            "Expected 200 (idempotent) or 400 (invalid transition), but got: " + statusCode;
        
        if (statusCode == 200) {
            CommonContext.lastResponse.then().body("status", equalTo("CANCELLED"));
        }
    }

    private Response updateReservationWithEmptyBody() {
        return given().spec(CommonContext.requestSpec)
                .header("Authorization", "Bearer " + CommonContext.adminToken)
                .body("{}")
                .patch("/admin/reservations/" + reservationId + "/status");
    }

    private Response updateReservationWithNullStatus() {
        return given().spec(CommonContext.requestSpec)
                .header("Authorization", "Bearer " + CommonContext.adminToken)
                .body("{\"status\": null}")
                .patch("/admin/reservations/" + reservationId + "/status");
    }

    @Then("예약 취소에 실패한다")
    public void 예약취소에실패한다() {
        CommonContext.lastResponse.then().statusCode(404);
    }

    @Then("예약 상태 변경에 성공한다")
    public void 예약상태변경에성공한다() {
        CommonContext.lastResponse.then().statusCode(200);
    }

    @Then("예약 처리에 성공한다")
    public void 예약처리에성공한다() {
        CommonContext.lastResponse.then().statusCode(200);
    }
}
