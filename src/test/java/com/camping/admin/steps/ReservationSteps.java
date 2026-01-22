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

public class ReservationSteps extends CucumberSpringConfiguration{

    @LocalServerPort
    private int port;

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation savedReservation;

    @Autowired
    private JwtService jwtService;

    @Value("${admin.username}")
    private String adminUsername;

    private String adminToken;

    @Before(order = 0)
    public void setupRestAssured() {
        RestAssured.port = port;
        System.out.println(">>> [Before] RestAssured 기본 설정 완료.");
    }

    @Before
    public void setupAdminToken() {
        this.adminToken = jwtService.generateToken(adminUsername);
    }

    @Given("사용자가 캠핑장 예약을 했다")
    public void userHasMadeReservation() {
        this.savedReservation = reservationRepository.findById(1L).orElseThrow(RuntimeException::new);
        System.out.println(">>> [Given] 사용자가 캠핑장 예약을 하는 단계를 수행합니다.");
    }

    @When("관리자가 해당 예약을 취소하면")
    public void adminCancelsTheReservation() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"CANCELLED\"}")
                .when()
                .patch("/admin/reservations/" + savedReservation.getId() + "/status");
        System.out.println(">>> [When] 관리자가 해당 예약을 취소하는 단계를 수행합니다.");
    }

    @Then("예약이 성공적으로 취소된다")
    public void reservationIsSuccessfullyCancelled() {
        System.out.println(">>> [Then] 예약이 성공적으로 취소되었는지 확인하는 단계를 수행합니다.");
    }
}
