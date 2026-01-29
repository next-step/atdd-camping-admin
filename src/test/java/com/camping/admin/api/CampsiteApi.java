package com.camping.admin.api;

import io.restassured.response.Response;

import java.util.Map;

import static com.camping.admin.api.ApiSupport.*;

public class CampsiteApi {

    // ===== 생성 =====

    public static Response 캠프사이트_생성(String token, String siteNumber, String description, int maxPeople) {
        return authRequest(token)
                .body(Map.of(
                        "siteNumber", siteNumber,
                        "description", description,
                        "maxPeople", maxPeople
                ))
                .post("/admin/campsites");
    }

    public static Response 캠프사이트_생성_인증없이(String siteNumber, String description, int maxPeople) {
        return baseRequest()
                .body(Map.of(
                        "siteNumber", siteNumber,
                        "description", description,
                        "maxPeople", maxPeople
                ))
                .post("/admin/campsites");
    }

    // ===== 수정 =====

    public static Response 캠프사이트_수정(String token, Long campsiteId, Map<String, Object> body) {
        return authRequest(token)
                .body(body)
                .put("/admin/campsites/" + campsiteId);
    }

    public static Response 캠프사이트_수정_인증없이(Long campsiteId, Map<String, Object> body) {
        return baseRequest()
                .body(body)
                .put("/admin/campsites/" + campsiteId);
    }

    // ===== 조회 =====

    public static Response 목록_조회(String token) {
        return authRequest(token)
                .get("/admin/campsites");
    }

    public static Response 목록_조회_인증없이() {
        return baseRequest()
                .get("/admin/campsites");
    }
}