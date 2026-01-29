package com.camping.admin.dto;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductRequest {

    @NotBlank
    private String name;

    @PositiveOrZero
    private Integer stockQuantity;

    @PositiveOrZero
    private BigDecimal price;

    private ProductType productType;

    public CreateProductRequest(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }

    public Product toEntity() {
        return new Product(
                name,
                stockQuantity != null ? stockQuantity : 0,
                price != null ? price : BigDecimal.ZERO,
                productType != null ? productType : ProductType.SALE
        );
    }
}