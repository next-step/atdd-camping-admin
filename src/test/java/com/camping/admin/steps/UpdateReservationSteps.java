package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static com.camping.admin.helper.CommonContext.lastResponse;
import static com.camping.admin.helper.RequestSender.patch;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("예약 상태 업데이트 테스트")
public class UpdateReservationSteps {

    private static final String RESERVATIONS_PATH = "/admin/reservations";
    private Long reservationId;
    private String currentStatus;

    @Given("예약이 존재한다")
    public void 예약이_존재한다() {
        reservationId = 1L; // data.sql의 예약 데이터 사용
    }

    @And("예약의 현재 상태가 {string}이다")
    public void 예약의_현재_상태가_설정된다(String status) {
        // 먼저 예약 상태를 지정된 상태로 설정
        Map<String, Object> statusRequest = new HashMap<>();
        statusRequest.put("status", status);
        
        patch(RESERVATIONS_PATH + "/" + reservationId + "/status", statusRequest);
        
        this.currentStatus = status;
    }

    @When("관리자가 예약 상태를 {string}로 변경한다")
    public void 관리자가_예약_상태를_변경한다(String status) {
        Map<String, Object> request = new HashMap<>();
        request.put("status", status);
        
        lastResponse = patch(RESERVATIONS_PATH + "/" + reservationId + "/status", request);
    }

    @When("관리자가 예약 ID {word}로 상태 업데이트를 시도한다")
    public void 관리자가_예약_ID로_상태_업데이트를_시도한다(String reservationId) {
        Map<String, Object> request = new HashMap<>();
        request.put("status", "CONFIRMED");
        
        String url;
        if ("null".equals(reservationId)) {
            url = RESERVATIONS_PATH + "/null/status";
        } else {
            url = RESERVATIONS_PATH + "/" + reservationId + "/status";
        }
        
        lastResponse = patch(url, request);
    }

    @When("관리자가 빈 요청 본문으로 상태 업데이트를 시도한다")
    public void 관리자가_빈_요청_본문으로_상태_업데이트를_시도한다() {
        Map<String, Object> emptyRequest = new HashMap<>();
        
        lastResponse = patch(RESERVATIONS_PATH + "/" + reservationId + "/status", emptyRequest);
    }

    @When("관리자가 {string} 상태값으로 업데이트를 시도한다")
    public void 관리자가_특정_상태값으로_업데이트를_시도한다(String statusType) {
        Map<String, Object> request = new HashMap<>();
        
        if ("\"\"".equals(statusType) || "".equals(statusType)) {
            request.put("status", "");
        } else if ("null".equals(statusType)) {
            request.put("status", null);
        } else {
            request.put("status", statusType);
        }
        
        lastResponse = patch(RESERVATIONS_PATH + "/" + reservationId + "/status", request);
    }

    @Then("예약 상태 업데이트에 성공했다")
    public void 예약_상태_업데이트에_성공했다() {
        lastResponse.then()
                .statusCode(OK.value());
    }

    @Then("예약 상태 업데이트가 실패한다")
    public void 예약_상태_업데이트가_실패한다() {
        lastResponse.then()
                .statusCode(BAD_REQUEST.value());
    }

    @And("예약 상태가 {string}로 변경되었다")
    public void 예약_상태가_변경되었다(String expectedStatus) {
        lastResponse.then()
                .body("status", equalTo(expectedStatus));
    }

    @And("예약 상태가 {string}로 유지되었다")
    public void 예약_상태가_유지되었다(String expectedStatus) {
        lastResponse.then()
                .body("status", equalTo(expectedStatus));
    }
}
