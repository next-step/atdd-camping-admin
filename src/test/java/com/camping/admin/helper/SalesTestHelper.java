package com.camping.admin.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map;

/**
 * 판매 도메인 테스트 헬퍼
 */
public class SalesTestHelper {

    private final RequestSpecification requestSpec;

    public SalesTestHelper(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    // === API 호출 ===

    public Response getSales() {
        return RestAssured.given()
            .spec(requestSpec)
            .when()
            .get("/api/sales");
    }

    public Response createSale(int productId, int quantity) {
        return RestAssured.given()
            .spec(requestSpec)
            .body(Map.of(
                "items", List.of(Map.of(
                    "productId", productId,
                    "quantity", quantity
                ))
            ))
            .when()
            .post("/api/sales");
    }

    public Response createSale(List<Map<String, Object>> items) {
        return RestAssured.given()
            .spec(requestSpec)
            .body(Map.of("items", items))
            .when()
            .post("/api/sales");
    }
}