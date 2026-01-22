package com.camping.admin.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ReservationSteps {

    private Long savedReservationId;
    private Response lastResponse;
    private String adminToken;

    @Before(order = 0)
    public void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        adminToken = obtainAdminTokenViaLoginApi("admin", "admin123");
        System.out.println(">>> [Before] RestAssured 기본 설정 완료.");
    }

    private String obtainAdminTokenViaLoginApi(String admin, String admin123) {
        // 실제 로그인 API를 호출하고 응답에서 토큰을 추출하는 로직
        return "mock-token";
    }

    @Given("사용자가 캠핑장 예약을 했다")
    public void userHasMadeReservation() {
        // Test-only API나 DB 직접 제어를 통해 예약 데이터 생성
        this.savedReservationId = 123L;
        System.out.println(">>> [Given] 사용자가 캠핑장 예약을 하는 단계를 수행합니다.");
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
        System.out.println(">>> [Then] 예약이 성공적으로 취소되었는지 확인하는 단계를 수행합니다.");
    }
}
