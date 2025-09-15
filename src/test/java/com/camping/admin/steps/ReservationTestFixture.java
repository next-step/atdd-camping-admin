package com.camping.admin.steps;

import com.camping.admin.helper.HttpMethod;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.camping.admin.helper.ApiHelper.createExtractableResponseWithAuthorization;

public class ReservationTestFixture {
    public static ExtractableResponse<Response> 예약_상태_변경(long reservationId, Map<String, String> body) {
        return createExtractableResponseWithAuthorization(HttpMethod.PATCH, "/admin/reservations/" + reservationId + "/status", body);
    }

    public static Map<String, Object> 특정_예약_조회(long reservationId) {
        return 예약_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> ((Number) r.get("id")).longValue() == reservationId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다."));
    }

    public static ExtractableResponse<Response> 예약_목록_조회() {
        ExtractableResponse<Response> response = createExtractableResponseWithAuthorization(HttpMethod.GET, "/admin/reservations");
        if (response.statusCode() != 200) {
            throw new AssertionError("예약 목록 조회 실패: " + response.statusCode());
        }
        return response;
    }

    public static Map<String, Object> 예약상태가_CONFIRMED인_특정예약조회(String reservationStatus) {
        return 예약_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(s -> s.get("status").equals(reservationStatus))
                .findFirst()
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다."));
    }

    // 체크인/체크아웃 관련 헬퍼 메서드들
    public static ExtractableResponse<Response> 예약_체크인_처리(long reservationId) {
        return createExtractableResponseWithAuthorization(HttpMethod.PATCH, "/admin/reservations/" + reservationId + "/check-in");
    }

    public static ExtractableResponse<Response> 예약_체크아웃_처리(long reservationId) {
        return createExtractableResponseWithAuthorization(HttpMethod.PATCH, "/admin/reservations/" + reservationId + "/check-out");
    }

    public static Map<String, Object> 오늘_체크인_날짜인_예약_조회() {
        // 오늘 날짜를 "yyyy-MM-dd" 형식의 문자열로 준비
        String todayString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE); // "2025-09-15"

        // 현재 날짜에 체크인 가능한 CONFIRMED 상태 예약 조회
        JsonPath jsonPath = 예약_목록_조회().jsonPath();
        return jsonPath
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> "CONFIRMED".equals(r.get("status")))
                // startDate(String)를 오늘 날짜(String)와 비교
                .filter(r -> r.get("startDate").equals(todayString))
                .findFirst()
                .orElseThrow(() -> new AssertionError("체크인 가능한 예약을 찾을 수 없습니다."));
    }

    public static Map<String, Object> 체크인_날짜가_아직_되지_않은_예약_조회() {
        // 체크인 날짜가 아직 되지 않은 CONFIRMED 상태 예약 조회
        return 예약_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> "CONFIRMED".equals(r.get("status")))
                .filter(r -> {
                    Object startDateObj = r.get("startDate");
                    LocalDate startDate;
                    if (startDateObj instanceof LocalDate) {
                        startDate = (LocalDate) startDateObj;
                    } else if (startDateObj instanceof String) {
                        startDate = LocalDate.parse((String) startDateObj);
                    } else {
                        return false;
                    }
                    return startDate.isAfter(LocalDate.now());
                }).findFirst()
                .orElseThrow(() -> new AssertionError("체크인 날짜가 되지 않은 예약을 찾을 수 없습니다."));
    }

    public static Map<String, Object> 이미_체크인된_예약_조회() {
        // 이미 체크인된 예약 조회
        JsonPath jsonPath = 예약_목록_조회().jsonPath();
        return jsonPath
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> "CHECKED_IN".equals(r.get("status")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("체크인된 예약을 찾을 수 없습니다."));
    }

    public static Map<String, Object> 체크인하지_않은_예약_조회() {
        // 체크인하지 않은 CONFIRMED 상태 예약 조회
        return 예약_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> "CONFIRMED".equals(r.get("status")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("체크인하지 않은 예약을 찾을 수 없습니다."));
    }
}
