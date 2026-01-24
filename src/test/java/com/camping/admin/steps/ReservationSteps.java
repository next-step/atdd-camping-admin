package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.support.AuthSupport;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private TestContext testContext;

    @Autowired
    private AuthSupport authSupport;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Given("캠핑장에 {string} 사이트가 등록되어 있다")
    public void 캠핑장에_사이트가_등록되어_있다(String siteNumber) {
        Campsite campsite = campsiteRepository.save(new Campsite(siteNumber, "Test Description", 4));
        testContext.setCampsite(campsite);
    }

    @Given("관리자 로그인이 되어 있다")
    public void 관리자_로그인이_되어있다() throws Exception {
        authSupport.관리자_로그인이_되어있다();
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string} 이름으로 예약되어 있다")
    public void 캠핑장에_예약이_되어있다(String customerName, String siteNumber) {
        Campsite campsite = testContext.getCampsite();
        Reservation reservation = new Reservation(
                customerName,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7),
                campsite
        );
        reservation.setStatus(ReservationStatus.PENDING.name());
        Reservation savedReservation = reservationRepository.save(reservation);
        testContext.setReservationId(savedReservation.getId());
    }

    @When("관리자가 예약을 {string} 상태로 변경하면")
    public void 관리자가_예약_상태를_변경한다(String status) throws Exception {
        Long reservationId = testContext.getReservationId();
        String authToken = testContext.getAuthToken();

        ExtractableResponse<Response> response = RestAssured.given()
                .cookie("AUTH_TOKEN", authToken)
                .contentType(ContentType.JSON)
                .body(Map.of("status", status))
            .when()
                .patch("/admin/reservations/" + reservationId + "/status")
            .then()
                .extract();
        
        testContext.setResponse(response);
    }

    @Then("요청이 성공한다")
    public void 요청이_성공한다() {
        var response = testContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @And("그리고 예약 상태가 {string} 으로 변경되어 있다")
    public void 예약_상태가_변경되어_있다(String expectedStatus) {
        var response = testContext.getResponse();
        String actualStatus = response.jsonPath().getString("status");
        assertThat(actualStatus).isEqualTo(expectedStatus);
    }
}
