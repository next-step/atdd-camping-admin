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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.List;

import static com.camping.admin.TestConstants.예약상태_취소;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps extends CucumberSpringConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JwtService jwtService;

    @Value("${admin.username}")
    private String adminUsername;

    private String adminToken;

    private Reservation savedReservation;

    @Before(order = 0)
    public void setupRestAssured() {
        RestAssured.port = port;
    }

    @Before(order = 1)
    public void setupAdminToken() {
        this.adminToken = jwtService.generateToken(adminUsername);
    }

    @Given("사용자가 {long}번 사이트 예약을 했다")
    public void 사용자가_사이트를_예약했다(Long siteNo) {
        this.savedReservation = reservationRepository.findById(siteNo).orElseThrow(RuntimeException::new);
    }

    @When("관리자가 해당 사이트 예약을 취소하면")
    public void 관리자가_해당_사이트_예약을_취소한다() {
        RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"" + 예약상태_취소 + "\"}")
                .when()
                .patch("/admin/reservations/" + savedReservation.getId() + "/status");
    }

    @Then("예약이 성공적으로 취소된다")
    public void 예약이_성공적으로_취소된다() {
        String status = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("find { it.id == " + savedReservation.getId() + " }.status");

        assertThat(status).isEqualTo(예약상태_취소);
    }

    @Then("해당 사이트는 다시 예약이 가능하다")
    public void 해당_사이트는_다시_예약이_가능하다() {
        Long campsiteId = savedReservation.getCampsite().getId();
        LocalDate startDate = savedReservation.getStartDate();
        LocalDate endDate = savedReservation.getEndDate();

        List<Reservation> conflictingReservations = reservationRepository
                .findOverlappingReservations(campsiteId, startDate, endDate);

        assertThat(conflictingReservations).isEmpty();
    }
}
