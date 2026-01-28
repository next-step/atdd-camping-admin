package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {
    private String name;
    private Integer stockQuantity;
    private BigDecimal price;
    private ProductType productType;

    public ProductCreateRequest(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }

    public Integer getStockQuantity() {
        return stockQuantity != null ? stockQuantity : 0;
    }

    public BigDecimal getPrice() {
        return price != null ? price : BigDecimal.ZERO;
    }

    public ProductType getProductType() {
        return productType != null ? productType : ProductType.SALE;
    }
}
