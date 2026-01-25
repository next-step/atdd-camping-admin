package com.camping.admin.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

/**
 * 대여 도메인 테스트 헬퍼
 */
public class RentalTestHelper {

    // 테스트용 고정 대여 ID (data.sql 기준)
    public static final int UNRETURNED_RENTAL_ID = 1001;  // 미반납 대여
    public static final int RETURNED_RENTAL_ID = 1002;    // 반납 완료 대여

    private final RequestSpecification requestSpec;

    public RentalTestHelper(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    // === API 호출 ===

    public Response getRentals() {
        return RestAssured.given()
            .spec(requestSpec)
            .when()
            .get("/admin/rentals");
    }

    public Response createRental(int productId, int quantity) {
        return RestAssured.given()
            .spec(requestSpec)
            .body(Map.of(
                "productId", productId,
                "quantity", quantity
            ))
            .when()
            .post("/admin/rentals");
    }

    public Response returnRental(int rentalId) {
        return RestAssured.given()
            .spec(requestSpec)
            .when()
            .patch("/admin/rentals/" + rentalId + "/return");
    }

    // === 조회 유틸리티 ===

    public int findUnreturnedRentalId() {
        Response response = getRentals();
        return response.jsonPath()
            .getInt("find { it.isReturned == false }.id");
    }

    public int findReturnedRentalId() {
        Response response = getRentals();
        return response.jsonPath()
            .getInt("find { it.isReturned == true }.id");
    }

    public int getRentalProductId(int rentalId) {
        Response response = getRentals();
        return response.jsonPath()
            .getInt("find { it.id == " + rentalId + " }.productId");
    }

    public boolean isRentalReturned(int rentalId) {
        Response response = getRentals();
        return response.jsonPath()
            .getBoolean("find { it.id == " + rentalId + " }.isReturned");
    }
}