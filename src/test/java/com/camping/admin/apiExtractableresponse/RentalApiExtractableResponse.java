package com.camping.admin.apiExtractableresponse;

import com.camping.admin.common.CommonHooks;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class RentalApiExtractableResponse {

    public static Response 대여를_생성한다(Long productId, Integer quantity, Long reservationId) {
        Map<String, Object> body = new HashMap<>();
        body.put("productId", productId);
        body.put("quantity", quantity);
        if (reservationId != null) {
            body.put("reservationId", reservationId);
        }

        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/admin/rentals");
    }

    public static Response 대여를_반납한다(Long rentalRecordId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .when()
                .patch("/admin/rentals/" + rentalRecordId + "/return");
    }
}