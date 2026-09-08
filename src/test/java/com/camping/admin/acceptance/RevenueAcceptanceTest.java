package com.camping.admin.acceptance;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

// 이 클래스는 매출(Revenue) 도메인의 인수 테스트를 모은다. 티켓별 세부 내용은 각 @Nested 위 주석에 적는다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RevenueAcceptanceTest {

    @LocalServerPort
    private int port;

    private String accessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.config = RestAssured.config()
                .encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"));

        accessToken = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"admin\",\"password\":\"admin123\"}")
                .when().post("/auth/login")
                .then().statusCode(200)
                .extract().path("accessToken");
    }

    // T-2: 예약 #13(revenue-reset.sql)은 reservationDate와 createdAt이 다른 날이다.
    // generateRangeRevenueEntries(SalesService.java)가 항목 날짜로 createdAt 대신
    // reservationDate를 쓰도록 고쳐, 일별 리포트가 매출로 잡은 날과 개별 항목 내역에
    // 찍히는 날이 같아졌다 — 그린이다.
    @Nested
    class T2_매출리포트금액이화면마다다름 {

        @Test
        @Sql("/sql/revenue-reset.sql")
        void 예약매출은_일별리포트가_잡은_날과_개별항목내역에_찍히는_날이_같아야한다() {
            LocalDate reservationDate = LocalDate.now().plusDays(5);
            LocalDate rangeTo = LocalDate.now().plusDays(10);

            double dailyTotal = given()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().get("/admin/reports/revenue/daily?date=" + reservationDate)
                    .then().statusCode(200)
                    .extract().jsonPath().getDouble("totalReservationRevenue");

            assertThat(dailyTotal).isEqualTo(50000.0);

            String occurredAt = given()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().get("/admin/reports/revenue/range/entries?from=" + reservationDate + "&to=" + rangeTo)
                    .then().statusCode(200)
                    .extract().jsonPath()
                    .getString("find { it.title == '예약 #13' }.occurredAt");

            assertThat(occurredAt).isNotNull();
            LocalDate entryDate = LocalDateTime.parse(occurredAt).toLocalDate();

            assertThat(entryDate)
                    .as("일별 리포트가 매출로 잡은 날(%s)과 개별 항목 내역에 찍힌 날(%s)이 같아야 한다",
                            reservationDate, entryDate)
                    .isEqualTo(reservationDate);
        }
    }
}
