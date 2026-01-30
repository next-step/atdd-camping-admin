package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

import static java.util.Objects.*;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    public static Product create(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        var product = new Product();

        product.name = name;
        product.stockQuantity = requireNonNullElse(stockQuantity, 0);
        product.price = requireNonNullElse(price, BigDecimal.ZERO);
        product.productType = requireNonNullElse(productType, ProductType.SALE);

        return product;
    }

    public void update(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = requireNonNullElse(name, this.name);
        this.stockQuantity = requireNonNullElse(stockQuantity, this.stockQuantity);
        this.price = requireNonNullElse(price, this.price);
        this.productType = requireNonNullElse(productType, this.productType);
    }

    public void decreaseStock(Integer quantity) {
        if (stockQuantity < quantity) {
            throw new IllegalStateException("Not enough stock for product " + this.name);
        }
        stockQuantity -= quantity;
    }

    public void increaseStock(Integer quantity) {
        stockQuantity += quantity;
    }
}
