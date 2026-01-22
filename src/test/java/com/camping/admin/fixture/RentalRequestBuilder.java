package com.camping.admin.fixture;

import java.util.HashMap;
import java.util.Map;

public class RentalRequestBuilder {

    private Long productId = TestConfig.ProductIds.LANTERN;
    private Integer quantity = 1;
    private Long reservationId = null;

    public static RentalRequestBuilder builder() {
        return new RentalRequestBuilder();
    }

    public static Map<String, Object> 기본_대여_요청() {
        return builder().build();
    }

    public RentalRequestBuilder productId(Long productId) {
        this.productId = productId;
        return this;
    }

    public RentalRequestBuilder quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public RentalRequestBuilder reservationId(Long reservationId) {
        this.reservationId = reservationId;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> request = new HashMap<>();
        request.put("productId", productId);
        request.put("quantity", quantity);
        if (reservationId != null) {
            request.put("reservationId", reservationId);
        }
        return request;
    }
}
