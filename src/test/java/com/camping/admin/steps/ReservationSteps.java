package com.camping.admin.steps;

import com.camping.admin.steps.api.ReservationApi;
import com.camping.admin.steps.context.TestContext;
import com.camping.admin.steps.support.TestDataFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
public class ReservationSteps {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private TestContext testContext;

    @Autowired
    private ReservationApi reservationApi;

    @Given("{string} 상태의 예약이 등록되어 있다")
    public void reservationWithStatusIsRegistered(String status) {
        Long reservationId = testDataFactory.createReservation("Test Customer", status);
        testContext.setLastReservationId(reservationId);
    }

    @When("관리자가 해당 예약의 상태를 {string}로 변경하면")
    public void adminUpdatesReservationStatus(String status) {
        testContext.setLastResponse(reservationApi.예약_상태_변경_요청(testContext.getAdminToken(), testContext.getLastReservationId(), status));
    }

    @When("관리자가 존재하지 않는 예약 ID의 상태를 {string}로 변경하면")
    public void adminUpdatesNonExistentReservationStatus(String status) {
        testContext.setLastResponse(reservationApi.예약_상태_변경_요청(testContext.getAdminToken(), 9999L, status));
    }

    @Then("예약 상태가 {string}로 변경된다")
    public void reservationStatusShouldBe(String expectedStatus) {
        // API 응답 검증
        assertThat(testContext.getLastResponse().statusCode()).isEqualTo(200);
        assertThat(testContext.getLastResponse().jsonPath().getString("status")).isEqualTo(expectedStatus);

        // 목록 조회를 통한 재검증 (Repository 대신 API 사용)
        ExtractableResponse<Response> response = reservationApi.예약_목록_조회_요청(testContext.getAdminToken());
        List<Map<String, Object>> reservations = response.jsonPath().getList("");
        
        Map<String, Object> reservation = reservations.stream()
                .filter(r -> r.get("id").toString().equals(testContext.getLastReservationId().toString()))
                .findFirst()
                .orElseThrow();
        
        assertThat(reservation.get("status")).isEqualTo(expectedStatus);
    }

    @Then("예약 상태 변경 요청이 실패한다")
    public void reservationStatusUpdateShouldFail() {
        assertThat(testContext.getLastResponse().statusCode()).isGreaterThanOrEqualTo(400);
    }

    @When("관리자가 예약 목록을 조회하면")
    public void adminFetchesAllReservations() {
        testContext.setLastResponse(reservationApi.예약_목록_조회_요청(testContext.getAdminToken()));
    }

    @Then("{int}개의 예약 목록이 조회된다")
    public void reservationListSizeShouldBe(int size) {
        List<?> list = testContext.getLastResponse().jsonPath().getList("");
        assertThat(list.size()).isGreaterThanOrEqualTo(size);
    }

    @Then("조회된 예약 목록에 {string} 상태의 예약이 포함되어 있어야 한다")
    public void reservationListShouldContainStatus(String status) {
        List<String> statuses = testContext.getLastResponse().jsonPath().getList("status");
        assertThat(statuses).contains(status);
    }
}