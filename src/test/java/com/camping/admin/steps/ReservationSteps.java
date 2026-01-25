package com.camping.admin.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    private Long savedReservationId;
    private Response lastResponse;
    private String adminToken;

    @Before(order = 1)
    public void setupAdminToken() {
        adminToken = obtainAdminTokenViaLoginApi("admin", "admin123");
        System.out.println(">>> [Before] 관리자 토큰 생성 완료.");
    }

    private String obtainAdminTokenViaLoginApi(String username, String password) {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/auth/login");

        return response.jsonPath().getString("accessToken");
    }

    @Given("사용자가 캠핑장 예약을 했다")
    public void userHasMadeReservation() {
        // data.sql에서 로드된 기존 예약 데이터 사용 (ID=1)
        this.savedReservationId = 1L;
        System.out.println(">>> [Given] 예약 데이터 생성 완료: ID " + savedReservationId);
    }

    @When("관리자가 해당 예약을 취소하면")
    public void adminCancelsTheReservation() {
        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"CANCELLED\"}")
                .when()
                .patch("/admin/reservations/" + savedReservationId + "/status");
        System.out.println(">>> [When] 관리자가 해당 예약을 취소하는 단계를 수행합니다.");
    }

    @Then("예약이 성공적으로 취소된다")
    public void reservationIsSuccessfullyCancelled() {
        assertThat(lastResponse.statusCode()).isEqualTo(200);
        assertThat(lastResponse.jsonPath().getString("status")).isEqualTo("CANCELLED");
        System.out.println(">>> [Then] 예약이 성공적으로 취소되었는지 확인하는 단계를 수행합니다.");
    }

    @And("관리자가 이미 해당 예약을 취소했다")
    public void adminAlreadyCancelledTheReservation() {
        RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"CANCELLED\"}")
                .when()
                .patch("/admin/reservations/" + savedReservationId + "/status");
        System.out.println(">>> [And] 관리자가 이미 해당 예약을 취소했습니다.");
    }

    @When("관리자가 다시 해당 예약을 취소하면")
    public void adminCancelsTheReservationAgain() {
        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"CANCELLED\"}")
                .when()
                .patch("/admin/reservations/" + savedReservationId + "/status");
        System.out.println(">>> [When] 관리자가 다시 해당 예약을 취소하는 단계를 수행합니다.");
    }

    @Then("시스템 정책에 맞는 결과가 반환된다")
    public void systemPolicyResultIsReturned() {
        // 현재 시스템 정책: 이미 취소된 예약을 다시 취소해도 200 OK 반환
        assertThat(lastResponse.statusCode()).isEqualTo(200);
        System.out.println(">>> [Then] 시스템 정책에 맞는 결과가 반환되었는지 확인하는 단계를 수행합니다.");
    }
}
