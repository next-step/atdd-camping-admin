package com.camping.admin.steps;

import com.camping.admin.helper.HttpMethod;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static com.camping.admin.helper.ApiHelper.createExtractableResponseWithAuthorization;
import static org.assertj.core.api.Assertions.assertThat;

public class RentalTestFixture {
    public static Map<String, Object> 특정_대여_기록_조회(long rentalId) {
        return 대여_기록_목록_조회().jsonPath()
                .<Map<String, Object>>getList("$").stream()
                .filter(r -> ((Integer) r.get("id")) == rentalId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("대여 기록을 찾을 수 없습니다."));
    }

    public static ExtractableResponse<Response> 대여_기록_목록_조회() {
        ExtractableResponse<Response> response = createExtractableResponseWithAuthorization(HttpMethod.GET, "/admin/rentals");
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<Response> 대여_기록_작성_요청(Map<String, Integer> body) {
        return createExtractableResponseWithAuthorization(HttpMethod.POST, "/admin/rentals", body);
    }
}
