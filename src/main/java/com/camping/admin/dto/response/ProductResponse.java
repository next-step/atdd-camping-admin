package com.camping.admin.dto.response;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        Integer stockQuantity,
        BigDecimal price,
        ProductType productType
) {
    public static ProductResponse from(Product entity) {
        return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getStockQuantity(),
                entity.getPrice(),
                entity.getProductType()
        );
    }
}
