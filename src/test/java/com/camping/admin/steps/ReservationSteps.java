package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

public class ReservationSteps extends CucumberSpringConfiguration{
    @LocalServerPort
    private int port;

    @Autowired
    private ReservationRepository reservationRepository;
    private Reservation savedReservation;
    private Response lastResponse;

    @Before(order = 0)
    public void setupRestAssured() {
      RestAssured.port = port;
      System.out.println("[Before] RestAssured 기본 설정 완료.");
    }

    @Before(order = 1)
    public void loginAndGetToken() {
      String token = RestAssured.given()
          .contentType(ContentType.JSON)
          .body("{\"username\":\"admin\",\"password\":\"admin123\"}")
          .when()
          .post("/auth/login")
          .then()
          .statusCode(200)
          .extract()
          .path("token");

      CommonContext.init(port, token);
      System.out.println("[Before] 관리자 로그인 완료, 토큰 발급됨.");
    }

    @Given("사용자가 캠핑장 예약을 했다")
    public void 사용자가캠핑장예약을했다() {
      // @Sql로 init하는 걸 하던가, mocking을 하던가
      // 불안정한 시스템 -> fake server 띄우기
      System.out.println("[Given] 사용자가 캠핑장 예약을 하는 단계를 수행합니다.");
      savedReservation = reservationRepository.findById(1L).orElseThrow(RuntimeException::new);
    }

    @When("관리자가 해당 예약을 취소하면")
    public void 관리자가해당예약을취소하면() {
      System.out.println("[When] 관리자가해당예약을취소하면");
      lastResponse = RestAssured.given()
          .spec(CommonContext.getRequestSpec())
          .body("{\"status\":\"CANCELLED\"}")
          .when()
          .patch("/admin/reservations/" + savedReservation.getId() + "/status");
    }

    @Then("예약이 성공적으로 취소된다")
    public void 예약이성공적으로취소된다() {
      System.out.println("[Then] 예약이성공적으로취소된다");
      lastResponse.then().statusCode(200);
    }
}