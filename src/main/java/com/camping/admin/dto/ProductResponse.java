package com.camping.admin.dto;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private Integer stockQuantity;
    private BigDecimal price;
    private ProductType productType;

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getStockQuantity(),
                product.getPrice(),
                product.getProductType()
        );
    }
}
