package com.camping.admin.dto;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
        ProductType resolvedType = resolveProductType();
        BigDecimal resolvedPrice = resolvePrice(resolvedType);
        int resolvedStock = resolveStock();

        return new Product(
                name,
                resolvedStock,
                resolvedPrice,
                resolvedType
        );
    }

    private ProductType resolveProductType() {
        return productType != null ? productType : ProductType.SALE;
    }

    public BigDecimal resolvePrice(ProductType productType) {
        BigDecimal resolvedPrice = price != null ? price : BigDecimal.valueOf(1000);

        if (productType == ProductType.SALE &&
                resolvedPrice.compareTo(BigDecimal.ONE) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "가격은 1원 이상이어야 합니다"
            );
        }

        return resolvedPrice;
    }

    private int resolveStock() {
        return stockQuantity != null ? stockQuantity : 0;
    }
}