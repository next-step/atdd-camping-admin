package com.camping.admin.steps;

import com.camping.admin.CucumberSpringConfiguration;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.security.JwtService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps extends CucumberSpringConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JwtService jwtService;

    private Reservation savedReservation;
    private String adminToken;
    private Response response;

    @Before(order = 0)
    public void setupRestAssured() {
        RestAssured.port = port;
        adminToken = jwtService.generateToken("admin");
    }

    @Given("사용자가 캠핑장 예약을 했다")
    public void userHasMadeReservation() {
        this.savedReservation = reservationRepository.findById(1L).orElseThrow(RuntimeException::new);
    }

    @When("관리자가 해당 예약을 취소하면")
    public void adminCancelsReservation() {
        response = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"status\": \"CANCELLED\"}")
                .when()
                .patch("/admin/reservations/" + savedReservation.getId() + "/status");
    }

    @Then("예약이 성공적으로 취소된다")
    public void reservationIsCancelled() {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo("CANCELLED");
    }

}
