package com.camping.admin.api;

import io.restassured.response.Response;

import java.util.Map;

import static com.camping.admin.api.ApiSupport.*;

public class ReservationApi {

    // ===== 상태 변경 =====

    public static Response 상태_변경(String token, Long reservationId, String status) {
        return authRequest(token)
                .body(Map.of("status", status))
                .patch("/admin/reservations/" + reservationId + "/status");
    }

    public static Response 상태_변경_인증없이(Long reservationId, String status) {
        return baseRequest()
                .body(Map.of("status", status))
                .patch("/admin/reservations/" + reservationId + "/status");
    }

    // ===== 조회 =====

    public static Response 목록_조회(String token) {
        return authRequest(token)
                .get("/admin/reservations");
    }

    public static Response 목록_조회_인증없이() {
        return baseRequest()
                .get("/admin/reservations");
    }
}
