package com.camping.admin.dto.request;

import com.camping.admin.domain.enums.ProductType;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        Integer stockQuantity,
        BigDecimal price,
        ProductType productType
) {
}
