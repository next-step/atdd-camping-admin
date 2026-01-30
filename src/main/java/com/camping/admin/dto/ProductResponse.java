package com.camping.admin.dto;

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
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getStockQuantity(),
                product.getPrice(),
                product.getProductType()
        );
    }
}
