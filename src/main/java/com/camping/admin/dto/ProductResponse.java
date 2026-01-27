package com.camping.admin.dto;

import com.camping.admin.domain.entity.Product;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private Integer stockQuantity;
    private BigDecimal price;
    private String productType;

    public static ProductResponse from(Product product) {
        return new ProductResponse(product);
    }

    private ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.stockQuantity = product.getStockQuantity();
        this.price = product.getPrice();
        this.productType = product.getProductType().name();
    }
}