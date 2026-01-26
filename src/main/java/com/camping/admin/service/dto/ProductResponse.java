package com.camping.admin.service.dto;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductResponse {
    private Long id;

    private String name;

    private Integer stockQuantity;

    private BigDecimal price;

    private ProductType productType;

    public ProductResponse(Long id, String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.id = id;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getStockQuantity(), product.getPrice(), product.getProductType());
    }
}
