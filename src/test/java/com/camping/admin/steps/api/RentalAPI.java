package com.camping.admin.steps.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RentalAPI {

    @Autowired
    private TestContext testContext;

    /**
     * 대여 생성 API 호출
     * POST /admin/rentals
     */
    public ExtractableResponse<Response> 대여_생성(Long productId, Integer quantity, Long reservationId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("productId", productId);
        requestBody.put("quantity", quantity);
        if (reservationId != null) {
            requestBody.put("reservationId", reservationId);
        }

        return RestAssured
                .given()
                    .header("Authorization", "Bearer " + testContext.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                .when()
                    .post("/admin/rentals")
                .then()
                    .extract();
    }
}