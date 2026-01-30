package com.camping.admin.dto;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import java.math.BigDecimal;
import lombok.Getter;

/**
 * 상품 응답 DTO
 * - 엔티티 직접 노출 대신 DTO 사용
 */
@Getter
public class ProductResponse {

    private final Long id;
    private final String name;
    private final Integer stockQuantity;
    private final BigDecimal price;
    private final ProductType productType;

    private ProductResponse(Long id, String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.id = id;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }

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