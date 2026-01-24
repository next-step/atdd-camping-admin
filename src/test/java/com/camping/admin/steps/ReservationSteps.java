package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    private String authToken;
    private ExtractableResponse<Response> response;
    private Long reservationId;
    private Campsite campsite;

    @Before
    public void setUp() {
        RestAssured.port = port;

        reservationRepository.deleteAllInBatch();
        campsiteRepository.deleteAllInBatch();
    }

    @Given("캠핑장에 {string} 사이트가 등록되어 있다")
    public void registerCampsite(String siteNumber) {
        campsite = campsiteRepository.save(new Campsite(siteNumber, "Test Description", 4));
    }

    @Given("관리자로 로그인 되어 있다")
    public void loginAsAdmin() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        authToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
            .when()
                .post("/auth/login")
            .then()
                .statusCode(200)
                .extract()
                .cookie("AUTH_TOKEN");
    }

    @Given("사이트 번호가 {string}인 캠핑장에 {string} 이름으로 예약되어 있다")
    public void createReservation(String customerName, String siteNumber) {
        Reservation reservation = new Reservation(
                customerName,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7),
                campsite
        );
        reservation.setStatus(ReservationStatus.PENDING.name());
        Reservation savedReservation = reservationRepository.save(reservation);
        this.reservationId = savedReservation.getId();
    }

    @When("관리자가 예약을 {string} 상태로 변경하면")
    public void updateReservationStatus(String status) throws Exception {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("status", status);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        response = RestAssured.given()
                .cookie("AUTH_TOKEN", authToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .patch("/admin/reservations/" + this.reservationId + "/status")
            .then()
                .extract();
    }

    @Then("요청이 성공한다")
    public void verifyRequestSuccess() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @And("그리고 예약 상태가 {string} 으로 변경되어 있다")
    public void verifyReservationStatusChanged(String expectedStatus) {
        var actual = response.jsonPath().getString("status");
        assertThat(actual).isEqualTo(expectedStatus);
    }
}
