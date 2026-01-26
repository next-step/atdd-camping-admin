package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class RentalApi {

    public static Response 대여를_생성한다(String token, Long productId, Integer quantity, Long reservationId) {
        Map<String, Object> body = new HashMap<>();
        body.put("productId", productId);
        body.put("quantity", quantity);
        if (reservationId != null) {
            body.put("reservationId", reservationId);
        }

        return RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(body)
                .post("/admin/rentals");
    }
}
