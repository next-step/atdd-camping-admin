package com.camping.admin.steps;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
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

    @Autowired
    private ReservationRepository reservationRepository;
    private Reservation reservation;
    private Response lastResponse;

    @LocalServerPort
    private int port;

    private String adminToken;

    @Value("${admin.username}")
    private String adminUsername;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Given("사용자가 캠핑장 예약을 했다")
    public void userHasMadeReservation() {
        System.out.println(">>> [Given] 사용자가 캠핑장 예약을 하는 단계를 수행합니다.");
        reservation = reservationRepository.findById(1L).orElseThrow(RuntimeException::new);
    }

    @When("관리자가 해당 예약을 취소하면")
    public void adminCancelsTheReservation() {
        System.out.println(">>> [When] 관리자가 해당 예약을 취소하는 단계를 수행합니다.");
        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken) // 인증 헤더 추가
                .contentType(ContentType.JSON)
                .body("{\"status\":\"CANCELLED\"}")
                .when()
                .patch("/admin/reservations/" +  reservation.getId() + "/status");
    }

    @Then("예약이 성공적으로 취소된다")
    public void reservationIsSuccessfullyCancelled() {
        System.out.println(">>> [Then] 예약이 성공적으로 취소되었는지 확인하는 단계를 수행합니다.");
    }
}
