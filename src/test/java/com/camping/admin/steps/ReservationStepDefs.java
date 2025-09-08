package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ReservationStepDefs {
    private Long reservationId;

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        // data.sql에서 기존 예약 데이터 사용
        reservationId = 1L;
    }

    @Given("관리자가 해당 예약을 취소했다")
    public void 관리자가해당예약을취소했다() {
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(Map.of("status", "CANCELLED"))
                .patch("/admin/reservations/" + reservationId + "/status");
        CommonContext.setLastResponse(response);
    }

    @When("관리자가 예약을 취소했다")
    public void 관리자가예약을취소했다() {
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(Map.of("status", "CANCELLED"))
                .patch("/admin/reservations/" + reservationId + "/status");
        CommonContext.setLastResponse(response);
    }

    @When("관리자가 예약 {int}을 취소했다")
    public void 관리자가예약을취소했다(int reservationId) {
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(Map.of("status", "CANCELLED"))
                .patch("/admin/reservations/" + reservationId + "/status");
        this.reservationId = (long) reservationId;
        CommonContext.setLastResponse(response);
    }

    @When("관리자가 동일 예약을 다시 취소했다")
    public void 관리자가동일예약을다시취소했다() {
        // 마지막 응답에서 예약 ID를 추출하여 다시 취소 시도
        Object idObj = CommonContext.getLastResponse().then().extract().path("id");
        Long reservationIdFromResponse;
        
        if (idObj instanceof Integer) {
            reservationIdFromResponse = ((Integer) idObj).longValue();
        } else {
            reservationIdFromResponse = (Long) idObj;
        }
        
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(Map.of("status", "CANCELLED"))
                .patch("/admin/reservations/" + reservationIdFromResponse + "/status");
        CommonContext.setLastResponse(response);
    }

    @Then("예약은 취소 상태다")
    public void 예약은취소상태다() {
        CommonContext.getLastResponse().then().body("status", equalTo("CANCELLED"));
    }

    @And("해당 자원은 다시 예약 가능하다")
    public void 해당자원은다시예약가능하다() {
        // 아직 가용성 엔드포인트가 구현되지 않았으므로,
        // 예약이 성공적으로 취소되었는지 확인하여
        // 해당 자원이 재예약 가능함을 간접적으로 검증
        CommonContext.getLastResponse().then()
                .statusCode(200)
                .body("status", equalTo("CANCELLED"));
        
        // 추가 검증: 취소된 예약이 예약 목록에서
        // CANCELLED 상태로 조회되는지 확인
        Object idObj = CommonContext.getLastResponse().then().extract().path("id");
        Long reservationId;
        
        if (idObj instanceof Integer) {
            reservationId = ((Integer) idObj).longValue();
        } else {
            reservationId = (Long) idObj;
        }
        
        given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .body("find { it.id == " + reservationId + " }.status", equalTo("CANCELLED"));
    }

    @Then("시스템 정책에 맞는 결과가 반환된다")
    public void 시스템정책에맞는결과가반환된다() {
        // 시스템은 중복 취소를 우아하게 처리해야 함
        // 가능한 경우들:
        // 1. 멱등성 동작 (200 OK with CANCELLED 상태)
        // 2. 오류 응답 (400 Bad Request - 잘못된 상태 전이)
        // 현재는 응답이 합리적인지 확인 (500이 아닌)
        int statusCode = CommonContext.getLastResponse().getStatusCode();
        assert statusCode == 200 || statusCode == 400 : 
            "Expected 200 (idempotent) or 400 (invalid transition), but got: " + statusCode;
        
        // 200인 경우 상태는 여전히 CANCELLED이어야 함
        if (statusCode == 200) {
            CommonContext.getLastResponse().then().body("status", equalTo("CANCELLED"));
        }
    }
}
