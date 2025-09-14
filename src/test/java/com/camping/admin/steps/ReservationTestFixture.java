package com.camping.admin.steps;

import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.helper.HttpMethod;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

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
}
