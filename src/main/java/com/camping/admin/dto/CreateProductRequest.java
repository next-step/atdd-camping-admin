package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank
    private String name;

    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @NotNull
    private ProductType productType;
}
