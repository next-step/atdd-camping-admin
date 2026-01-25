package com.camping.admin.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 매출 리포트 도메인 테스트 헬퍼
 */
public class RevenueTestHelper {

    private final RequestSpecification requestSpec;

    public RevenueTestHelper(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    // === API 호출 ===

    public Response getDailyRevenue(String date) {
        return RestAssured.given()
            .spec(requestSpec)
            .queryParam("date", date)
            .when()
            .get("/admin/reports/revenue/daily");
    }

    public Response getDailyRevenue(LocalDate date) {
        return getDailyRevenue(date.format(DateTimeFormatter.ISO_DATE));
    }

    public Response getRangeRevenue(String from, String to) {
        return RestAssured.given()
            .spec(requestSpec)
            .queryParam("from", from)
            .queryParam("to", to)
            .when()
            .get("/admin/reports/revenue/range");
    }

    public Response getRangeRevenue(LocalDate from, LocalDate to) {
        return getRangeRevenue(
            from.format(DateTimeFormatter.ISO_DATE),
            to.format(DateTimeFormatter.ISO_DATE)
        );
    }

    // === 유틸리티 ===

    public Response getTodayRevenue() {
        return getDailyRevenue(LocalDate.now());
    }

    public Response getCurrentMonthRevenue() {
        LocalDate now = LocalDate.now();
        return getRangeRevenue(now.withDayOfMonth(1), now);
    }
}