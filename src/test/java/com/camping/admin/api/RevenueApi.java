package com.camping.admin.api;

import io.restassured.response.Response;

import static com.camping.admin.api.ApiSupport.*;

public class RevenueApi {

    // ===== 일별 매출 =====

    public static Response 일별_매출_조회(String token, String date) {
        return authRequest(token)
                .queryParam("date", date)
                .get("/admin/reports/revenue/daily");
    }

    public static Response 일별_매출_조회_인증없이(String date) {
        return baseRequest()
                .queryParam("date", date)
                .get("/admin/reports/revenue/daily");
    }

    // ===== 기간별 매출 =====

    public static Response 기간별_매출_조회(String token, String from, String to) {
        return authRequest(token)
                .queryParam("from", from)
                .queryParam("to", to)
                .get("/admin/reports/revenue/range");
    }

    public static Response 기간별_매출_조회_인증없이(String from, String to) {
        return baseRequest()
                .queryParam("from", from)
                .queryParam("to", to)
                .get("/admin/reports/revenue/range");
    }

    // ===== 기간별 매출 상세 =====

    public static Response 기간별_매출_상세_조회(String token, String from, String to) {
        return authRequest(token)
                .queryParam("from", from)
                .queryParam("to", to)
                .get("/admin/reports/revenue/range/entries");
    }

    public static Response 기간별_매출_상세_조회_인증없이(String from, String to) {
        return baseRequest()
                .queryParam("from", from)
                .queryParam("to", to)
                .get("/admin/reports/revenue/range/entries");
    }
}