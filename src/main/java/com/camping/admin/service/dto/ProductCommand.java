package com.camping.admin.service.dto;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.google.common.base.Preconditions;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductCommand {
    private final String name;
    private final Integer stockQuantity;
    private final BigDecimal price;
    private final ProductType productType;

    public ProductCommand(String name, Integer stockQuantity, BigDecimal price, String productType) {
        Preconditions.checkNotNull(name, "Product name must not be null.");
        Preconditions.checkNotNull(stockQuantity, "Stock quantity must not be null.");
        Preconditions.checkNotNull(price, "Price must not be null.");

        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = ProductType.find(productType);
    }

    public Product toEntity() {
        return new Product(name, stockQuantity, price, productType);
    }
}
