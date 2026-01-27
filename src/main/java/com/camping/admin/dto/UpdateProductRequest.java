package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class UpdateProductRequest {
    private String name;
    private Integer stockQuantity;
    private BigDecimal price;
    private ProductType productType;
}
