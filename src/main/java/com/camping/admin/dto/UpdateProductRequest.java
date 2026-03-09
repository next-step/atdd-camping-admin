package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequest {
    private String name;
    private Integer stockQuantity;
    private BigDecimal price;
    private ProductType productType;
}
