package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class RentalApi {

    // ===== 공통 =====
    private static RequestSpecification baseRequest() {
        return RestAssured.given()
                .contentType(ContentType.JSON);
    }

    private static RequestSpecification authRequest(String token) {
        return baseRequest()
                .header("Authorization", "Bearer " + token);
    }

    // ===== 대여 =====

    public static Response 대여_생성(String token, Long productId, int quantity) {
        return authRequest(token)
                .body(Map.of("productId", productId, "quantity", quantity))
                .post("/admin/rentals");
    }

    public static Response 대여_생성_인증없이(Long productId, int quantity) {
        return baseRequest()
                .body(Map.of("productId", productId, "quantity", quantity))
                .post("/admin/rentals");
    }

    public static Response 대여_생성_with_예약(String token, Long productId, int quantity, Long reservationId) {
        return authRequest(token)
                .body(Map.of("productId", productId, "quantity", quantity, "reservationId", reservationId))
                .post("/admin/rentals");
    }

    // ===== 반납 =====

    public static Response 반납(String token, Long rentalRecordId) {
        return authRequest(token)
                .patch("/admin/rentals/" + rentalRecordId + "/return");
    }

    public static Response 반납_인증없이(Long rentalRecordId) {
        return baseRequest()
                .patch("/admin/rentals/" + rentalRecordId + "/return");
    }

    // ===== 조회 =====

    public static Response 목록_조회(String token) {
        return authRequest(token)
                .get("/admin/rentals");
    }

    public static Response 목록_조회_인증없이() {
        return baseRequest()
                .get("/admin/rentals");
    }
}
