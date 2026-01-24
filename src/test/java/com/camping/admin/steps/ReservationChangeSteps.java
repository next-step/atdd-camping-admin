package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.security.JwtService;
import com.camping.admin.support.ScenarioContext;
import com.camping.admin.support.TestDataFactory;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationChangeSteps extends CucumberSpringConfiguration {

    private static final String INVALID_STATUS = "INVALID_STATUS";

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private TestDataFactory testDataFactory;

    @Value("${admin.username}")
    private String adminUsername;

    private String adminToken;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        adminToken = jwtService.generateToken(adminUsername);
    }

    // ===== 공통 헬퍼 메서드 =====

    private void givenReservationWithStatus(String status) {
        Reservation reservation = testDataFactory.getReservationWithStatus(status);
        scenarioContext.setReservation(reservation);
        assertThat(reservation.getStatus()).isEqualTo(status);
    }

    private void requestStatusChange(String body) {
        Long reservationId = scenarioContext.getReservationId();

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch("/admin/reservations/" + reservationId + "/status");

        scenarioContext.setResponse(response);
    }

    private void assertDbStatus(String expectedStatus) {
        Reservation reservation = scenarioContext.getReservation();
        Reservation updated = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(expectedStatus);
    }

    // ===== Given =====

    @Given("확정된 예약이 있다")
    public void 확정된_예약이_있다() {
        givenReservationWithStatus("CONFIRMED");
    }

    @Given("존재하지 않는 예약 ID가 주어진다")
    public void 존재하지_않는_예약_ID가_주어진다() {
        scenarioContext.setReservationId(99999L);
    }

    @Given("취소된 예약이 있다")
    public void 취소된_예약이_있다() {
        givenReservationWithStatus("CANCELLED");
    }

    // ===== When =====

    @When("관리자가 해당 예약을 취소한다")
    public void 관리자가_해당_예약을_취소한다() {
        requestStatusChange("{\"status\":\"CANCELLED\"}");
    }

    @When("관리자가 빈 요청을 보낸다")
    public void 관리자가_빈_요청을_보낸다() { // body가 아예 없는 경우
        requestStatusChange("{}");
    }

    @When("관리자가 상태값 없이 요청한다")
    public void 관리자가_상태값_없이_요청한다() { // 상태 필드가 없는 경우
        requestStatusChange("{\"other\":\"value\"}");
    }

    @When("관리자가 유효하지 않은 상태값으로 변경을 요청한다")
    public void 관리자가_유효하지_않은_상태값으로_변경을_요청한다() {
        scenarioContext.setRequestedStatus(INVALID_STATUS);
        requestStatusChange("{\"status\":\"" + INVALID_STATUS + "\"}");
    }

    @When("관리자가 빈 문자열로 상태 변경을 요청한다")
    public void 관리자가_빈_문자열로_상태_변경을_요청한다() { // 빈 문자열인 경우
        requestStatusChange("{\"status\":\"\"}");
    }

    // ===== Then =====

    @Then("예약은 취소 상태다")
    public void 예약은_취소_상태다() {
        var response = scenarioContext.getResponse();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo("CANCELLED");
        assertDbStatus("CANCELLED");
    }

    @Then("예약을 찾을 수 없다는 오류가 발생한다")
    public void 예약을_찾을_수_없다는_오류가_발생한다() {
        var response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isIn(400, 404, 500);
    }

    @Then("잘못된 요청이라는 응답을 받는다")
    public void 잘못된_요청이라는_응답을_받는다() {
        var response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Then("예약은 기존 상태를 유지한다")
    public void 예약은_기존_상태를_유지한다() {
        String originalStatus = scenarioContext.getOriginalStatus();
        assertDbStatus(originalStatus);
    }

    @Then("예약 상태가 해당 값으로 변경된다")
    public void 예약_상태가_해당_값으로_변경된다() {
        var response = scenarioContext.getResponse();
        String requestedStatus = scenarioContext.getRequestedStatus();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo(requestedStatus);
        assertDbStatus(requestedStatus);
    }
}