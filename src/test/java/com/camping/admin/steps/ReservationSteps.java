package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 예약 관련 인수테스트 Step 정의 클래스
 *
 * [중요] CucumberSpringConfiguration을 상속하지 않습니다.
 * Spring 컨텍스트는 CucumberSpringConfiguration에서 설정되고,
 * 이 클래스는 @Autowired를 통해 빈을 주입받습니다.
 */
public class ReservationSteps {

    /**
     * 테스트 서버의 랜덤 포트
     * @SpringBootTest(RANDOM_PORT)로 실행된 서버의 포트를 주입받습니다.
     */
    @LocalServerPort
    private int port;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

    /**
     * 시나리오 내에서 공유되는 상태값들
     * - accessToken: 로그인 후 발급받은 JWT 토큰
     * - response: API 호출 결과
     * - reservationId: 생성된 예약의 ID (When/Then에서 사용)
     */
    private String accessToken;
    private ExtractableResponse<Response> response;
    private Long reservationId;

    /**
     * 각 시나리오 실행 전에 호출됩니다.
     * RestAssured의 기본 포트를 설정합니다.
     */
    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    // ==================== Background ====================

    /**
     * 관리자 로그인을 수행하고 JWT 토큰을 저장합니다.
     * POST /auth/login
     *
     * 이 토큰은 이후 인증이 필요한 API 호출 시 사용됩니다.
     */
    @Given("관리자로 로그인되어 있다")
    public void 관리자_로그인() {
        // application.yml의 admin.username, admin.password 사용
        Map<String, String> loginRequest = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        // 로그인 API 호출
        ExtractableResponse<Response> loginResponse = RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(loginRequest)
                .when()
                    .post("/auth/login")
                .then()
                    .extract();

        // JWT 토큰 저장
        this.accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    // ==================== Given ====================

    /**
     * 테스트에 필요한 예약 데이터를 생성합니다.
     *
     * @param customerName 고객명 (예: "김철수")
     * @param siteNumber   캠프사이트 번호 (예: "A-001")
     * @param status       예약 상태 (예: "CONFIRMED")
     */
    @Given("{string} 고객의 {string} 캠프사이트 예약이 {string} 상태로 존재한다")
    public void 예약이_존재한다(String customerName, String siteNumber, String status) {
        // 1. 캠프사이트 준비 (없으면 생성)
        Campsite campsite = campsiteRepository.findBySiteNumber(siteNumber)
                .orElseGet(() -> {
                    Campsite newCampsite = new Campsite(siteNumber, "테스트 캠프사이트", 4);
                    return campsiteRepository.save(newCampsite);
                });

        // 2. 예약 생성
        Reservation reservation = new Reservation(
                customerName,
                LocalDate.now().plusDays(1),  // 내일부터
                LocalDate.now().plusDays(3),  // 3일 후까지
                campsite
        );
        reservation.setStatus(status);
        reservation.setReservationDate(LocalDate.now());

        // 3. 저장 및 ID 보관 (When/Then에서 사용)
        Reservation saved = reservationRepository.save(reservation);
        this.reservationId = saved.getId();
    }

    // ==================== When ====================

    /**
     * 예약 상태 변경 API를 호출합니다.
     * PATCH /admin/reservations/{id}/status
     *
     * [중요] Authorization 헤더에 JWT 토큰을 포함합니다.
     *
     * @param newStatus 변경할 상태값 (예: "CANCELLED")
     */
    @When("관리자가 해당 예약을 {string} 상태로 변경한다")
    public void 예약_상태를_변경한다(String newStatus) {
        // API 요청 본문
        Map<String, String> requestBody = Map.of("status", newStatus);

        // API 호출 (인증 토큰 포함)
        this.response = RestAssured
                .given()
                    .header("Authorization", "Bearer " + accessToken)  // JWT 토큰
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                .when()
                    .patch("/admin/reservations/{id}/status", reservationId)
                .then()
                    .extract();
    }

    // ==================== Then ====================

    /**
     * 예약 상태가 올바르게 변경되었는지 검증합니다.
     * 1. API 응답 상태코드 확인
     * 2. API 응답 본문의 status 확인
     * 3. DB에 실제로 반영되었는지 확인
     *
     * @param expectedStatus 기대하는 상태값 (예: "CANCELLED")
     */
    @Then("예약 상태가 {string}로 변경된다")
    public void 예약_상태_확인(String expectedStatus) {
        // 1. API 응답 상태코드 검증 (200 OK)
        assertThat(response.statusCode())
                .as("API 응답 상태코드")
                .isEqualTo(HttpStatus.OK.value());

        // 2. API 응답 본문 검증
        assertThat(response.jsonPath().getString("status"))
                .as("API 응답의 예약 상태")
                .isEqualTo(expectedStatus);

        // 3. DB 상태 검증 (실제로 변경되었는지 확인)
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다: " + reservationId));

        assertThat(reservation.getStatus())
                .as("DB에 저장된 예약 상태")
                .isEqualTo(expectedStatus);
    }
}