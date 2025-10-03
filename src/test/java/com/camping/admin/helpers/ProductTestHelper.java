package com.camping.admin.helpers;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProductTestHelper {

    private static final String PRODUCT_ID_KEY = "productId";

    public static Long getProductId() {
        return ContextHelper.get(PRODUCT_ID_KEY, Long.class);
    }

    public static void setProductId(Long productId) {
        ContextHelper.set(PRODUCT_ID_KEY, productId);
    }

    public static void setResponse(Response response) {
        ContextHelper.setResponse(response);
    }

    public static Response getLastResponse() {
        return ContextHelper.getLastResponse();
    }

    public static Response createProduct(String name, Integer stockQuantity, BigDecimal price, String productType) {
        Map<String, Object> request = Map.of(
                "name", name,
                "stockQuantity", stockQuantity,
                "price", price,
                "productType", productType
        );

        return ApiHelper.givenAuthenticated()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/admin/products");
    }

    public static Response createProductWithoutName(Integer stockQuantity, BigDecimal price, String productType) {
        Map<String, Object> request = new HashMap<>();
        request.put("stockQuantity", stockQuantity);
        request.put("price", price);
        request.put("productType", productType);

        return ApiHelper.givenAuthenticated()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/admin/products");
    }

    public static Response getAllProducts() {
        return ApiHelper.givenAuthenticated()
                .when()
                .get("/admin/products");
    }

    public static Response updateProduct(Long productId, String name, Integer stockQuantity, BigDecimal price, String productType) {
        Map<String, Object> request = Map.of(
                "name", name,
                "stockQuantity", stockQuantity,
                "price", price,
                "productType", productType
        );

        return ApiHelper.givenAuthenticated()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/admin/products/" + productId);
    }

    public static Long createAndGetProductId(String name, Integer stockQuantity, BigDecimal price, String productType) {
        Response response = createProduct(name, stockQuantity, price, productType);
        return response.jsonPath().getLong("id");
    }
}
