package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {
    private String name;
    private Integer stockQuantity;
    private BigDecimal price;
    private ProductType productType;

    public ProductUpdateRequest(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }
}
