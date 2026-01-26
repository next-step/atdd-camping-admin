package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.utils.DatabaseCleaner;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import static com.camping.admin.domain.enums.ReservationStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestContext testContext;

    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    @Before(order = 2)
    public void setUp() {
        databaseCleaner.execute();
    }

    // ==========================================
    // Given - 사전 조건 준비
    // ==========================================

    @Given("캠핑장에 {string} 사이트가 등록되어 있다")
    @Transactional
    public void 캠핑장에_사이트가_등록되어_있다(String siteNumber) {
        Campsite campsite = campsiteRepository.save(new Campsite(siteNumber, "Test Description", 4));
        testContext.addCampsite(siteNumber, campsite);
    }

    @Given("관리자 로그인이 되어 있다")
    public void 관리자_로그인이_되어있다() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        String authToken = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .cookie("AUTH_TOKEN");

        testContext.setAuthToken(authToken);
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string}이 대기 상태로 예약되어 있다")
    @Transactional
    public void 캠핑장에_대기중인_예약이_있다(String siteNumber, String customerName) {
        Campsite campsite = testContext.getCampsite(siteNumber);

        Reservation reservation = new Reservation(
            customerName,
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(3),
            campsite);
        reservation.setStatus(PENDING.name());

        Reservation savedReservation = reservationRepository.save(reservation);
        testContext.addReservation(customerName, savedReservation);
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string} 이름으로 취소 상태의 예약이 있다")
    @Transactional
    public void 캠핑장에_취소된_예약이_있다(String siteNumber, String customerName) {
        Campsite campsite = testContext.getCampsite(siteNumber);

        Reservation reservation = new Reservation(
            customerName,
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(3),
            campsite);
        reservation.setStatus(CANCELLED.name());

        Reservation savedReservation = reservationRepository.save(reservation);
        testContext.addReservation(customerName, savedReservation);
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string} 이름으로 확정된 예약이 있다")
    @Transactional
    public void 캠핑장에_확정된_예약이_있다(String siteNumber, String customerName) {
        Campsite campsite = testContext.getCampsite(siteNumber);

        Reservation reservation = new Reservation(
                customerName,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                campsite);
        reservation.setStatus(CONFIRMED.name());

        Reservation savedReservation = reservationRepository.save(reservation);
        testContext.addReservation(customerName, savedReservation);
    }

    // ==========================================
    // When - API 행위 실행
    // ==========================================

    @When("관리자가 예약을 확정하면")
    public void 관리자가_예약을_확정하면() {
        var requestBody = Map.of("status", CONFIRMED.name());
        var reservationId = testContext.getReservationId();
        var authToken = testContext.getAuthToken();

        var response = RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .patch("/admin/reservations/" + reservationId + "/status")
            .then()
            .extract();

        testContext.setResponse(response);
    }

    @When("관리자가 존재하지 않는 예약\\(ID {long}\\)의 상태를 확정하려고 하면")
    public void 관리자가_존재하지_않는_예약의_상태를_확정하려고_하면(long reservationId) {
        var requestBody = Map.of("status", CONFIRMED.name());
        var authToken = testContext.getAuthToken();

        var response = RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .patch("/admin/reservations/" + reservationId + "/status")
            .then()
            .extract();

        testContext.setResponse(response);
    }

    @When("관리자가 잘못된 본문\\({string}\\)으로 예약 상태 변경을 요청하면")
    public void 관리자가_잘못된_본문으로_예약_상태를_변경을_요청하면(String invalidBody) {
        var reservationId = testContext.getReservationId();
        var authToken = testContext.getAuthToken();

        var response = RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .contentType(ContentType.JSON)
            .body(invalidBody)
            .when()
            .patch("/admin/reservations/" + reservationId + "/status")
            .then()
            .extract();

        testContext.setResponse(response);
    }

    @When("관리자가 예약 목록을 조회하면")
    public void 관리자가_예약_목록을_조회하면() {
        var authToken = testContext.getAuthToken();

        var response = RestAssured.given()
            .cookie("AUTH_TOKEN", authToken)
            .when()
            .get("/admin/reservations")
            .then()
            .extract();

        testContext.setResponse(response);
    }

    // ==========================================
    // Then - 검증
    // ==========================================

    @Then("요청이 성공한다")
    public void 요청이_성공한다() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @And("예약이 확정된다")
    public void 예약이_확정된다() {
        var response = testContext.getResponse();
        String actualStatus = response.jsonPath().getString("status");
        assertThat(actualStatus).isEqualTo(CONFIRMED.name());
    }

    @Then("요청이 실패한다\\({int}\\)")
    public void 요청이_실패한다(int expectedStatusCode) {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(expectedStatusCode);
    }

    @Then("전체 예약 목록의 개수는 {int}개이다")
    public void 전체_예약_목록의_개수는_N개이다(int count) {
        var response = testContext.getResponse();
        assertThat(response.jsonPath().getList("$")).hasSize(count);
    }
}
