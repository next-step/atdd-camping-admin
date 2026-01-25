package com.camping.admin.controller.dto;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.service.dto.ProductCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateProductRequest(@NotBlank String name,
                                   @Min(0) Integer stockQuantity,
                                   @DecimalMin(value = "0.0") BigDecimal price,
                                   String productType) {

    public CreateProductRequest(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this(name, stockQuantity, price, productType.name());
    }

    public ProductCommand toServiceDto() {
        return new ProductCommand(name, stockQuantity, price, productType);
    }
}