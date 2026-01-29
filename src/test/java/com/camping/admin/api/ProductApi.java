package com.camping.admin.api;

import io.restassured.response.Response;

import java.util.Map;

import static com.camping.admin.api.ApiSupport.*;

public class ProductApi {

    // ===== 생성 =====

    public static Response 상품_생성(String token, String name, int stockQuantity, int price, String productType) {
        return authRequest(token)
                .body(Map.of(
                        "name", name,
                        "stockQuantity", stockQuantity,
                        "price", price,
                        "productType", productType
                ))
                .post("/admin/products");
    }

    public static Response 상품_생성_인증없이(String name, int stockQuantity, int price, String productType) {
        return baseRequest()
                .body(Map.of(
                        "name", name,
                        "stockQuantity", stockQuantity,
                        "price", price,
                        "productType", productType
                ))
                .post("/admin/products");
    }

    // ===== 수정 =====

    public static Response 상품_수정(String token, Long productId, Map<String, Object> body) {
        return authRequest(token)
                .body(body)
                .put("/admin/products/" + productId);
    }

    public static Response 상품_수정_인증없이(Long productId, Map<String, Object> body) {
        return baseRequest()
                .body(body)
                .put("/admin/products/" + productId);
    }

    // ===== 조회 =====

    public static Response 목록_조회(String token) {
        return authRequest(token)
                .get("/admin/products");
    }

    public static Response 목록_조회_인증없이() {
        return baseRequest()
                .get("/admin/products");
    }
}
