package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RentalAPI {

    /**
     * 대여 생성 API 호출
     * POST /admin/rentals
     */
    public ExtractableResponse<Response> 대여_생성(String token, Long productId, Integer quantity, Long reservationId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("productId", productId);
        requestBody.put("quantity", quantity);
        if (reservationId != null) {
            requestBody.put("reservationId", reservationId);
        }

        return RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                .when()
                    .post("/admin/rentals")
                .then()
                    .extract();
    }
}