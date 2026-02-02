package com.camping.admin.dto;

public record SaleItemRequest(
        Long productId,
        Integer quantity
) {
}
