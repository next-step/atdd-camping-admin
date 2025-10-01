package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public Product(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }

    public void updateProduct(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }

    public void decreaseStock(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Not enough stock for product " + this.name +
                ". Available: " + this.stockQuantity + ", Requested: " + quantity);
        }
        this.stockQuantity -= quantity;
    }

    public void increaseStock(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockQuantity += quantity;
    }

    public boolean isAvailable() {
        return this.stockQuantity > 0;
    }

    public BigDecimal calculateTotalPrice(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return this.price.multiply(new BigDecimal(quantity));
    }

    public boolean isRentalProduct() {
        return this.productType == ProductType.RENTAL;
    }

    public void validateRentalProduct() {
        if (!isRentalProduct()) {
            throw new IllegalArgumentException("Product is not a rental item.");
        }
    }
}