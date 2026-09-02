package com.camping.admin;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({
        @Sql(
                scripts = {"/sql/clear-revenue-data.sql", "/sql/t3-cancelled-reservation-revenue-fixture.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        ),
        @Sql(
                scripts = {"/sql/clear-revenue-data.sql", "/data.sql"},
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        )
})
class CancelledReservationRevenueAcceptanceTest {

    private static final long RESERVATION_ID = 3001L;
    private static final String RESERVATION_DATE = "2026-08-04";
    private static final String RESERVATION_TITLE = "예약 #3001";

    @LocalServerPort
    private int port;

    @Test
    void shouldExcludeCancelledReservationFromAllRevenueReports() {
        String accessToken = login();

        Map<String, Object> reservationBeforeCancellation = findReservation(
                getReservations(accessToken),
                RESERVATION_ID);
        assertThat(reservationBeforeCancellation.get("status")).isEqualTo("CONFIRMED");

        Response dailyBeforeCancellation = getDailyReport(accessToken, RESERVATION_DATE);
        Response rangeBeforeCancellation = getRangeReport(
                accessToken,
                RESERVATION_DATE,
                RESERVATION_DATE);
        List<Map<String, Object>> entriesBeforeCancellation = getRangeEntries(
                accessToken,
                RESERVATION_DATE,
                RESERVATION_DATE);

        assertReportAmounts(dailyBeforeCancellation, "50000", "0", "0", "50000");
        assertReportAmounts(rangeBeforeCancellation, "50000", "0", "0", "50000");
        Map<String, Object> reservationEntry = findEntry(
                entriesBeforeCancellation,
                RESERVATION_TITLE);
        assertThat(reservationEntry.get("type")).isEqualTo("RESERVATION");
        assertAmount(reservationEntry, "50000");
        assertThat(reservationEntry.get("occurredAt").toString()).startsWith(RESERVATION_DATE);

        Response cancellationResponse = cancelReservation(accessToken, RESERVATION_ID);
        assertThat(((Number) cancellationResponse.jsonPath().get("id")).longValue())
                .isEqualTo(RESERVATION_ID);
        assertThat(cancellationResponse.jsonPath().getString("status")).isEqualTo("CANCELLED");

        Map<String, Object> storedReservation = findReservation(
                getReservations(accessToken),
                RESERVATION_ID);
        assertThat(storedReservation.get("status")).isEqualTo("CANCELLED");

        Response dailyAfterCancellation = getDailyReport(accessToken, RESERVATION_DATE);
        Response rangeAfterCancellation = getRangeReport(
                accessToken,
                RESERVATION_DATE,
                RESERVATION_DATE);
        List<Map<String, Object>> entriesAfterCancellation = getRangeEntries(
                accessToken,
                RESERVATION_DATE,
                RESERVATION_DATE);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(amount(dailyAfterCancellation, "totalReservationRevenue"))
                    .as("취소 후 일별 예약 매출")
                    .isEqualByComparingTo("0");
            softly.assertThat(amount(dailyAfterCancellation, "totalSalesRevenue"))
                    .as("취소 후 일별 판매 매출")
                    .isEqualByComparingTo("0");
            softly.assertThat(amount(dailyAfterCancellation, "totalRentalRevenue"))
                    .as("취소 후 일별 대여 매출")
                    .isEqualByComparingTo("0");
            softly.assertThat(amount(dailyAfterCancellation, "grandTotalRevenue"))
                    .as("취소 후 일별 총매출")
                    .isEqualByComparingTo("0");

            softly.assertThat(amount(rangeAfterCancellation, "totalReservationRevenue"))
                    .as("취소 후 기간 예약 매출")
                    .isEqualByComparingTo("0");
            softly.assertThat(amount(rangeAfterCancellation, "totalSalesRevenue"))
                    .as("취소 후 기간 판매 매출")
                    .isEqualByComparingTo("0");
            softly.assertThat(amount(rangeAfterCancellation, "totalRentalRevenue"))
                    .as("취소 후 기간 대여 매출")
                    .isEqualByComparingTo("0");
            softly.assertThat(amount(rangeAfterCancellation, "grandTotalRevenue"))
                    .as("취소 후 기간 총매출")
                    .isEqualByComparingTo("0");

            softly.assertThat(entriesAfterCancellation)
                    .as("취소 후 기간 상세")
                    .noneMatch(entry -> RESERVATION_TITLE.equals(entry.get("title")));
        });
    }

    private String login() {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("username", "admin", "password", "admin123"))
        .when()
                .post("/auth/login")
        .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    private List<Map<String, Object>> getReservations(String accessToken) {
        return given()
                .port(port)
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
        .when()
                .get("/admin/reservations")
        .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("$");
    }

    private Response cancelReservation(String accessToken, long reservationId) {
        return given()
                .port(port)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("status", "CANCELLED"))
        .when()
                .patch("/admin/reservations/{reservationId}/status", reservationId)
        .then()
                .statusCode(200)
                .extract()
                .response();
    }

    private Response getDailyReport(String accessToken, String date) {
        return given()
                .port(port)
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .queryParam("date", date)
        .when()
                .get("/admin/reports/revenue/daily")
        .then()
                .statusCode(200)
                .extract()
                .response();
    }

    private Response getRangeReport(String accessToken, String from, String to) {
        return given()
                .port(port)
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .queryParam("from", from)
                .queryParam("to", to)
        .when()
                .get("/admin/reports/revenue/range")
        .then()
                .statusCode(200)
                .extract()
                .response();
    }

    private List<Map<String, Object>> getRangeEntries(
            String accessToken,
            String from,
            String to
    ) {
        return given()
                .port(port)
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .queryParam("from", from)
                .queryParam("to", to)
        .when()
                .get("/admin/reports/revenue/range/entries")
        .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("$");
    }

    private void assertReportAmounts(
            Response report,
            String reservationRevenue,
            String salesRevenue,
            String rentalRevenue,
            String grandTotalRevenue
    ) {
        assertThat(amount(report, "totalReservationRevenue"))
                .isEqualByComparingTo(reservationRevenue);
        assertThat(amount(report, "totalSalesRevenue"))
                .isEqualByComparingTo(salesRevenue);
        assertThat(amount(report, "totalRentalRevenue"))
                .isEqualByComparingTo(rentalRevenue);
        assertThat(amount(report, "grandTotalRevenue"))
                .isEqualByComparingTo(grandTotalRevenue);
    }

    private BigDecimal amount(Response report, String field) {
        return new BigDecimal(report.jsonPath().get(field).toString());
    }

    private void assertAmount(Map<String, Object> entry, String expected) {
        assertThat(new BigDecimal(entry.get("amount").toString()))
                .isEqualByComparingTo(expected);
    }

    private Map<String, Object> findReservation(
            List<Map<String, Object>> reservations,
            long reservationId
    ) {
        return reservations.stream()
                .filter(reservation -> ((Number) reservation.get("id")).longValue() == reservationId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("예약 조회에서 찾지 못한 예약: " + reservationId));
    }

    private Map<String, Object> findEntry(
            List<Map<String, Object>> entries,
            String title
    ) {
        return entries.stream()
                .filter(entry -> title.equals(entry.get("title")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("상세 내역에서 찾지 못한 거래: " + title));
    }
}
