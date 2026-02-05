package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class CreateProductRequest {
    private String name;
    private Integer stockQuantity;
    private BigDecimal price;
    private ProductType productType;

    public CreateProductRequest(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }
}
