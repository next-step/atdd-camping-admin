package com.camping.admin;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({
        @Sql(
                scripts = {"/sql/clear-revenue-data.sql", "/sql/t2-revenue-fixture.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        ),
        @Sql(
                scripts = {"/sql/clear-revenue-data.sql", "/data.sql"},
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        )
})
class RevenueReportAcceptanceTest {

    private static final String RESERVATION_DATE = "2026-08-04";
    private static final String CREATED_DATE = "2026-08-03";
    private static final String CHECK_IN_DATE = "2026-08-05";
    private static final String RESERVATION_TITLE = "예약 #2001";
    private static final String REPORT_FROM = "2026-08-27";
    private static final String REPORT_TO = "2026-09-02";
    private static final String LINKED_RESERVATION_DATE = "2026-08-28";
    private static final String SALE_DATE = "2026-08-29";
    private static final String RENTAL_DATE = "2026-09-01";
    private static final String SALE_A_TITLE = "T-2 판매 상품 A 외";
    private static final String SALE_B_TITLE = "T-2 판매 상품 B 외";
    private static final String RENTAL_TITLE = "T-2 대여 상품 (예약#2002)";

    @LocalServerPort
    private int port;

    @Test
    void shouldAttributeSameReservationToReservationDateAcrossRevenueReports() {
        String accessToken = login();

        Response dailyReport = getDailyReport(accessToken, RESERVATION_DATE);
        Response rangeReport = getRangeReport(accessToken, RESERVATION_DATE, RESERVATION_DATE);
        List<Map<String, Object>> entries = getRangeEntries(
                accessToken,
                RESERVATION_DATE,
                RESERVATION_DATE);

        assertAmount(dailyReport, "totalReservationRevenue", "50000");
        assertAmount(rangeReport, "totalReservationRevenue", "50000");

        Map<String, Object> reservationEntry = findEntry(entries, RESERVATION_TITLE);
        assertThat(reservationEntry.get("type")).isEqualTo("RESERVATION");
        assertAmount(reservationEntry, "50000");
        assertThat(reservationEntry.get("occurredAt").toString()).startsWith(RESERVATION_DATE);
    }

    @Test
    void shouldNotAttributeReservationToCreationOrCheckInDate() {
        String accessToken = login();

        for (String date : List.of(CREATED_DATE, CHECK_IN_DATE)) {
            Response dailyReport = getDailyReport(accessToken, date);
            Response rangeReport = getRangeReport(accessToken, date, date);
            List<Map<String, Object>> entries = getRangeEntries(accessToken, date, date);

            assertAmount(dailyReport, "totalReservationRevenue", "0");
            assertAmount(rangeReport, "totalReservationRevenue", "0");
            assertThat(entries)
                    .noneMatch(entry -> "RESERVATION".equals(entry.get("type")));
        }
    }

    @Test
    void shouldMatchRangeSummaryAndEntryTotalsForSameMultiDayPeriod() {
        String accessToken = login();

        Response rangeReport = getRangeReport(accessToken, REPORT_FROM, REPORT_TO);
        List<Map<String, Object>> entries = getRangeEntries(accessToken, REPORT_FROM, REPORT_TO);

        assertReportAmounts(rangeReport, "200000", "40000", "20000", "260000");
        assertThat(sumAmounts(entries, "RESERVATION")).isEqualByComparingTo("200000");
        assertThat(sumAmounts(entries, "SALE")).isEqualByComparingTo("40000");
        assertThat(sumAmounts(entries, "RENTAL")).isEqualByComparingTo("20000");
        assertThat(sumAmounts(entries)).isEqualByComparingTo("260000");

        assertEntry(entries, SALE_A_TITLE, "SALE", "30000", SALE_DATE);
        assertEntry(entries, SALE_B_TITLE, "SALE", "10000", RENTAL_DATE);
        assertEntry(entries, RENTAL_TITLE, "RENTAL", "20000", RENTAL_DATE);
    }

    @Test
    void shouldAttributeSalesAndLinkedRentalToCreatedAtAcrossRevenueReports() {
        String accessToken = login();

        assertTransactionAcrossReports(
                accessToken,
                SALE_DATE,
                "totalSalesRevenue",
                SALE_A_TITLE,
                "SALE",
                "30000");
        assertTransactionAcrossReports(
                accessToken,
                RENTAL_DATE,
                "totalRentalRevenue",
                RENTAL_TITLE,
                "RENTAL",
                "20000");

        Response dailyReport = getDailyReport(accessToken, LINKED_RESERVATION_DATE);
        Response rangeReport = getRangeReport(
                accessToken,
                LINKED_RESERVATION_DATE,
                LINKED_RESERVATION_DATE);
        List<Map<String, Object>> entries = getRangeEntries(
                accessToken,
                LINKED_RESERVATION_DATE,
                LINKED_RESERVATION_DATE);

        assertAmount(dailyReport, "totalRentalRevenue", "0");
        assertAmount(rangeReport, "totalRentalRevenue", "0");
        assertThat(entries).noneMatch(entry -> "RENTAL".equals(entry.get("type")));
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
        assertAmount(report, "totalReservationRevenue", reservationRevenue);
        assertAmount(report, "totalSalesRevenue", salesRevenue);
        assertAmount(report, "totalRentalRevenue", rentalRevenue);
        assertAmount(report, "grandTotalRevenue", grandTotalRevenue);
    }

    private void assertAmount(Response report, String field, String expected) {
        assertThat(new BigDecimal(report.jsonPath().get(field).toString()))
                .isEqualByComparingTo(expected);
    }

    private void assertAmount(Map<String, Object> entry, String expected) {
        assertThat(new BigDecimal(entry.get("amount").toString()))
                .isEqualByComparingTo(expected);
    }

    private void assertEntry(
            List<Map<String, Object>> entries,
            String title,
            String type,
            String amount,
            String occurredOn
    ) {
        Map<String, Object> entry = findEntry(entries, title);
        assertThat(entry.get("type")).isEqualTo(type);
        assertAmount(entry, amount);
        assertOccurredOn(entry, occurredOn);
    }

    private void assertTransactionAcrossReports(
            String accessToken,
            String date,
            String reportField,
            String title,
            String type,
            String amount
    ) {
        Response dailyReport = getDailyReport(accessToken, date);
        Response rangeReport = getRangeReport(accessToken, date, date);
        List<Map<String, Object>> entries = getRangeEntries(accessToken, date, date);

        assertAmount(dailyReport, reportField, amount);
        assertAmount(rangeReport, reportField, amount);
        assertEntry(entries, title, type, amount, date);
    }

    private void assertOccurredOn(Map<String, Object> entry, String expectedDate) {
        assertThat(entry.get("occurredAt").toString())
                .startsWith(expectedDate);
    }

    private Map<String, Object> findEntry(List<Map<String, Object>> entries, String title) {
        return entries.stream()
                .filter(entry -> title.equals(entry.get("title")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("상세 내역에서 찾지 못한 거래: " + title));
    }

    private BigDecimal sumAmounts(List<Map<String, Object>> entries, String type) {
        return entries.stream()
                .filter(entry -> type.equals(entry.get("type")))
                .map(entry -> new BigDecimal(entry.get("amount").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumAmounts(List<Map<String, Object>> entries) {
        return entries.stream()
                .map(entry -> new BigDecimal(entry.get("amount").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
