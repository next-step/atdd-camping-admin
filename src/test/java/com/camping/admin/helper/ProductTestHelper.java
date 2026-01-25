package com.camping.admin.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.Map;

/**
 * 상품 도메인 테스트 헬퍼
 * - API 호출 메서드
 * - 테스트 데이터 상수
 * - 상태 관리
 */
public class ProductTestHelper {

    // 테스트용 고정 상품 ID (data.sql 기준)
    public static final int RENTAL_PRODUCT_ID = 1001;      // 랜턴 (RENTAL)
    public static final int SALE_PRODUCT_ID = 1002;        // 장작팩 (SALE)
    public static final int OUT_OF_STOCK_RENTAL_ID = 1013; // 품절 텐트 (RENTAL, 재고 0)
    public static final int OUT_OF_STOCK_SALE_ID = 1014;   // 품절 침낭 (SALE, 재고 0)
    public static final int LIMITED_STOCK_SALE_ID = 1015;  // 한정판 굿즈 (SALE, 재고 5)

    private final RequestSpecification requestSpec;

    // 상태 관리
    private int targetProductId;
    private int originalStock;

    public ProductTestHelper(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    // === API 호출 ===

    public Response getProducts() {
        return RestAssured.given()
            .spec(requestSpec)
            .when()
            .get("/admin/products");
    }

    public Response createProduct(String name, int stockQuantity, int price, String productType) {
        Map<String, Object> body = new HashMap<>();
        if (name != null) body.put("name", name);
        body.put("stockQuantity", stockQuantity);
        if (price > 0) body.put("price", price);
        body.put("productType", productType);

        return RestAssured.given()
            .spec(requestSpec)
            .body(body)
            .when()
            .post("/admin/products");
    }

    public Response updateProduct(int productId, String name, int stockQuantity, int price) {
        return RestAssured.given()
            .spec(requestSpec)
            .body(Map.of(
                "name", name,
                "stockQuantity", stockQuantity,
                "price", price
            ))
            .when()
            .put("/admin/products/" + productId);
    }

    // === 조회 유틸리티 ===

    public int findProductByTypeAndStock(String productType, int minStock) {
        Response response = getProducts();
        return response.jsonPath()
            .getInt("find { it.productType == '" + productType + "' && it.stockQuantity >= " + minStock + " }.id");
    }

    public int getProductStock(int productId) {
        Response response = getProducts();
        return response.jsonPath()
            .getInt("find { it.id == " + productId + " }.stockQuantity");
    }

    // === 상태 관리 ===

    public int getTargetProductId() {
        return targetProductId;
    }

    public void setTargetProductId(int targetProductId) {
        this.targetProductId = targetProductId;
    }

    public int getOriginalStock() {
        return originalStock;
    }

    public void setOriginalStock(int originalStock) {
        this.originalStock = originalStock;
    }
}