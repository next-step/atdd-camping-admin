package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;

import java.math.BigDecimal;

public record ProductCreateRequest(
        String name,
        Integer stockQuantity,
        BigDecimal price,
        ProductType productType
) {
}
