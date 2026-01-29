package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomerAPI {

    /**
     * 예약 요청 파라미터 생성
     */
    public Map<String, Object> 예약_요청_파라미터_생성(String siteNumber, String startDate, String endDate, String customerName, String email, String phoneNumber) {
        Map<String, Object> params = new HashMap<>();
        params.put("siteNumber", siteNumber);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("customerName", customerName);
        params.put("email", email);
        params.put("phoneNumber", phoneNumber);
        return params;
    }

    /**
     * 고객 상세 조회 API 호출
     * GET /admin/customers/{customerId}
     */
    public ExtractableResponse<Response> 고객_상세_조회(String token, Long customerId) {
        return RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/admin/customers/{customerId}", customerId)
                .then()
                    .extract();
    }
}
