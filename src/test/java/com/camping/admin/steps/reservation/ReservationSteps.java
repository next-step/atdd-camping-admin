package com.camping.admin.steps.reservation;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.utils.RequestSpecFixture;
import com.camping.admin.utils.ResponseAcceptanceFixture;
import com.camping.admin.utils.TestDataBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.camping.admin.domain.enums.ReservationStatus.*;
import static com.camping.admin.utils.RequestAcceptanceFixture.patchReservationStatus;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {
    private Long reservationId;
    private String adminToken;
    ExtractableResponse<Response> reserved;
    ExtractableResponse<Response> response;


    @Given("관리자가 로그인한다")
    public void 관리자가_로그인한다() {
        adminToken = RequestSpecFixture.fetchAdminToken();
    }

    /**
     * 테스트가 한 번 쭉 돌면 얘는 한 번만 돌텐데, 나머지 이 메서드가 필요한 구간에는 얘를 호출을 해야 할까? @BeforeEach로 해도 괜찮지 않을까?
     * 현재 시나리오들을 미루어 보아, Background에 해당 Given을 넣어도 될까?
     * 공식문서에도 각 시나리오마다 돈다고 나와있는 것 같은데...
     */
    @And("사용자가 예약했다")
    public void 사용자가_예약했다() {
        reservationId = 1L;
        Map<String, String> confirmed = TestDataBuilder.reservationStatusPayload(CONFIRMED.name());

        Response rawResponse = patchReservationStatus(adminToken, reservationId, confirmed);
        reserved = ResponseAcceptanceFixture.extract(rawResponse, HttpStatus.OK);
        String status = reserved.jsonPath().getString("status");
        assertThat(status).isEqualTo(CONFIRMED.name());
    }


    /**
     * Happy path : 관리자가 예약을 대기 상태로 바꿀 수 있다
     */
    @When("관리자가 예약을 {string} 상태로 바꾼다")
    public void 관리자가_예약을_대기상태로_바꾼다(String status) {
        String reservedStatus = reserved.jsonPath().getString("status");
        assertThat(reservedStatus).isEqualTo(CONFIRMED.name());

        Map<String, String> canceled = TestDataBuilder.reservationStatusPayload(status);
        Response rawResponse = patchReservationStatus(adminToken, reservationId, canceled);
        response = ResponseAcceptanceFixture.extract(rawResponse, HttpStatus.OK);
    }

    @Then("예약은 {string} 상태가 된다")
    public void 예약상태가_대기상태가_된다(String status) {
        String responseStatus = response.as(ReservationResponse.class).getStatus();
        assertThat(responseStatus).isEqualTo(status);
    }


    /**
     * Happy path : 예약을 관리자가 취소한 뒤 다시 예약할 수 있다
     */
    @When("관리자가 예약을 취소했다")
    public void 관리자가_예약을_취소했다() {
        Map<String, String> canceled = TestDataBuilder.reservationStatusPayload(CANCELED.name());
        Response rawResponse = patchReservationStatus(adminToken, reservationId, canceled);
        response = ResponseAcceptanceFixture.extract(rawResponse, HttpStatus.OK);
    }

    @Then("예약은 취소 상태다")
    public void 예약은_취소상태다() {
        String status = response.as(ReservationResponse.class).getStatus();
        assertThat(status).isEqualTo(CANCELED.name());
    }

    @And("재예약에 성공한다")
    public void 해당_캠핑장은_다시_예약가능하다() {
        Map<String, String> waiting = TestDataBuilder.reservationStatusPayload(WAITING.name());
        Response rawResponse = patchReservationStatus(adminToken, reservationId, waiting);
        response = ResponseAcceptanceFixture.extract(rawResponse, HttpStatus.OK);

        String status = response.jsonPath().getString("status");
        assertThat(status).isNotEqualTo(CONFIRMED.name()); // CONFIRMED 상태가 아니면 예약 성공
    }


    /**
     * Happy path : 이미 체크아웃된 예약을 다시 체크아웃하려 하면 상태가 그대로 유지된다
     */
    @When("관리자가 예약을 체크아웃 상태로 바꾼다")
    public void 관리자가_예약을_체크아웃상태로_바꾼다() {
        Map<String, String> checkedOut = TestDataBuilder.reservationStatusPayload(CHECKED_OUT.name());
        Response rawResponse = patchReservationStatus(adminToken, reservationId, checkedOut);
        response = ResponseAcceptanceFixture.extract(rawResponse, HttpStatus.OK);
    }

    @And("관리자가 체크아웃된 예약의 상태를 동일한 상태로 바꾼다")
    public void 관리자가_체크아웃된예약의상태를_동일한상태로_바꾼다() {
        Map<String, String> checkedOut = TestDataBuilder.reservationStatusPayload(CHECKED_OUT.name());
        Response rawResponse = patchReservationStatus(adminToken, reservationId, checkedOut);
        response = ResponseAcceptanceFixture.extract(rawResponse, HttpStatus.OK);
    }

    @Then("예약의 상태는 그대로 유지된다")
    public void 예약의상태는_그대로_유지된다() {
        String responseStatus = response.jsonPath().getString("status");
        assertThat(responseStatus).isEqualTo(CHECKED_OUT.name());
    }


    /**
     * Edge 케이스 : 예약 상태를 허용되지 않은 문자열로 변경하면 그대로 저장된다
     * TODO : EDGE 케이스를 막을 유효성 추가
     */
    @When("예약의 상태를 {string}으로 변경한다")
    public void 예약의상태를_엉뚱한문자열로_변경한다(String status) {
        Map<String, String> checkedOut = TestDataBuilder.reservationStatusPayload(status);
        Response rawResponse = patchReservationStatus(adminToken, reservationId, checkedOut);
        response = ResponseAcceptanceFixture.extract(rawResponse, HttpStatus.BAD_REQUEST);
    }

    @Then("예약의 상태 변경에 실패한다")
    public void 예약의_상태변경에_실패한다() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
