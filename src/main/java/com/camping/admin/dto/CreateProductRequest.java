package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductRequest {
    private String name;
    private Integer stockQuantity = 0;
    private BigDecimal price = BigDecimal.ZERO;
    private ProductType productType = ProductType.SALE;
}
