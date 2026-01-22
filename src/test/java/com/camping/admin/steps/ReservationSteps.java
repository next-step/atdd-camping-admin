package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.steps.api.ReservationAPI;
import com.camping.admin.steps.api.TestContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


public class ReservationSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestContext testContext;  // 공유 저장소

    @Autowired
    private ReservationAPI reservationAPI;  // API 호출 담당

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * @param customerName 고객명 (예: "김철수")
     * @param siteNumber   캠프사이트 번호 (예: "A-001")
     * @param status       예약 상태 (예: "CONFIRMED")
     */
    @Given("{string} 고객의 {string} 캠프사이트 예약이 {string} 상태로 존재한다")
    public void 예약이_존재한다(String customerName, String siteNumber, String status) {

        Campsite campsite = campsiteRepository.findBySiteNumber(siteNumber)
                .orElseGet(() -> campsiteRepository.save(
                        new Campsite(siteNumber, "테스트 캠프사이트", 4)));


        Reservation reservation = new Reservation(
                customerName,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                campsite
        );
        reservation.setStatus(status);
        reservation.setReservationDate(LocalDate.now());

        Reservation saved = reservationRepository.save(reservation);
        testContext.setReservationId(saved.getId());
    }


    /**
     * @param newStatus 변경할 상태값 (예: "CANCELLED")
     */
    @When("관리자가 해당 예약을 {string} 상태로 변경한다")
    public void 예약_상태를_변경한다(String newStatus) {
        var 예약_상태_변경 = reservationAPI.예약_상태_변경(newStatus);
        testContext.setResponse(예약_상태_변경);
    }

    /**
     * @param expectedStatus 기대하는 상태값 (예: "CANCELLED")
     */
    @Then("예약 상태가 {string}로 변경된다")
    public void 예약_상태_확인(String expectedStatus) {
        // 1. API 응답 상태코드 검증
        assertThat(testContext.getResponse().statusCode())
                .as("API 응답 상태코드")
                .isEqualTo(HttpStatus.OK.value());

        // 2. API 응답 본문 검증
        assertThat(testContext.getResponse().jsonPath().getString("status"))
                .as("API 응답의 예약 상태")
                .isEqualTo(expectedStatus);

        // 3. DB 상태 검증
        Reservation reservation = reservationRepository
                .findById(testContext.getReservationId())
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다"));

        assertThat(reservation.getStatus())
                .as("DB에 저장된 예약 상태")
                .isEqualTo(expectedStatus);
    }
}
