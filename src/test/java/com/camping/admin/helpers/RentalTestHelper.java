package com.camping.admin.helpers;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.Map;

public class RentalTestHelper {

    private static final String RENTAL_ID_KEY = "rentalId";
    private static final String PRODUCT_ID_KEY = "productId";

    public static Long getRentalId() {
        return ContextHelper.get(RENTAL_ID_KEY, Long.class);
    }

    public static void setRentalId(Long rentalId) {
        ContextHelper.set(RENTAL_ID_KEY, rentalId);
    }

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

    public static Response createRental(Long productId, Integer quantity, Long reservationId) {
        Map<String, Object> request;
        if (reservationId != null) {
            request = Map.of(
                    "productId", productId,
                    "quantity", quantity,
                    "reservationId", reservationId
            );
        } else {
            request = Map.of(
                    "productId", productId,
                    "quantity", quantity
            );
        }

        return ApiHelper.givenAuthenticated()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/admin/rentals");
    }

    public static Response returnRental(Long rentalId) {
        return ApiHelper.givenAuthenticated()
                .when()
                .patch("/admin/rentals/" + rentalId + "/return");
    }

    public static Response getAllRentals() {
        return ApiHelper.givenAuthenticated()
                .when()
                .get("/admin/rentals");
    }

    public static Long createRentalProduct(String name, Integer stockQuantity) {
        return ProductTestHelper.createAndGetProductId(name, stockQuantity, new BigDecimal("50000"), "RENTAL");
    }

    public static Long createSaleProduct(String name, Integer stockQuantity) {
        return ProductTestHelper.createAndGetProductId(name, stockQuantity, new BigDecimal("10000"), "SALE");
    }

    public static Long createAndGetRentalId(Long productId, Integer quantity) {
        Response response = createRental(productId, quantity, null);
        return response.jsonPath().getLong("id");
    }

    public static Integer getProductStock(Long productId) {
        Response response = ApiHelper.givenAuthenticated()
                .when()
                .get("/admin/products");

        return response.jsonPath()
                .getList("$")
                .stream()
                .filter(item -> {
                    Map<String, Object> product = (Map<String, Object>) item;
                    return productId.equals(((Number) product.get("id")).longValue());
                })
                .map(item -> {
                    Map<String, Object> product = (Map<String, Object>) item;
                    return ((Number) product.get("stockQuantity")).intValue();
                })
                .findFirst()
                .orElse(0);
    }
}
