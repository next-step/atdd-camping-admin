package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.security.JwtService;
import com.camping.admin.support.ScenarioContext;
import com.camping.admin.support.TestDataFactory;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationChangeSteps extends CucumberSpringConfiguration {

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

    @Given("확정된 예약이 있다")
    public void 확정된_예약이_있다() {
        Reservation reservation = testDataFactory.getConfirmedReservation();
        scenarioContext.setReservation(reservation);

        assertThat(reservation.getStatus()).isEqualTo("CONFIRMED");
    }

    @When("관리자가 해당 예약을 취소한다")
    public void 관리자가_해당_예약을_취소한다() {
        Reservation reservation = scenarioContext.getReservation();

        var response = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"CANCELLED\"}")
                .when()
                .patch("/admin/reservations/" + reservation.getId() + "/status");

        scenarioContext.setResponse(response);
    }

    @Then("예약은 취소 상태다")
    public void 예약은_취소_상태다() {
        var response = scenarioContext.getResponse();
        Reservation reservation = scenarioContext.getReservation();

        assertThat(response.statusCode()).isEqualTo(200);

        String actualStatus = response.jsonPath().getString("status");
        assertThat(actualStatus).isEqualTo("CANCELLED");

        Reservation updated = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo("CANCELLED");
    }
}