package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.security.JwtService;
import io.cucumber.java.Before;
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

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JwtService jwtService;

    @LocalServerPort
    private int port;

    @Value("${admin.username}")
    private String adminUsername;

    private String adminToken;
    private Reservation reservation;
    private Response response;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        adminToken = jwtService.generateToken(adminUsername);
    }

    @Given("확정된 예약이 있다")
    public void 확정된_예약이_있다() {
        reservation = reservationRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("테스트 데이터가 존재하지 않습니다."));
        assertThat(reservation.getStatus()).isEqualTo("CONFIRMED");
    }

    @When("관리자가 해당 예약을 취소한다")
    public void 관리자가_해당_예약을_취소한다() {
        response = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"CANCELLED\"}")
                .when()
                .patch("/admin/reservations/" + reservation.getId() + "/status");
    }

    @Then("예약은 취소 상태다")
    public void 예약은_취소_상태다() {
        assertThat(response.statusCode()).isEqualTo(200);

        String actualStatus = response.jsonPath().getString("status");
        assertThat(actualStatus).isEqualTo("CANCELLED");

        Reservation updated = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo("CANCELLED");
    }
}