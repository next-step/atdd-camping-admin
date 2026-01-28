package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    public boolean isRentalType() {
        return this.productType == ProductType.RENTAL;
    }

    public void decreaseStock(Integer quantity) {
        if (!isRentalType()) {
            throw new IllegalArgumentException("Product is not a rental item.");
        }

        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Not enough stock for product " + name);
        }

        this.stockQuantity -= quantity;
    }

    public void increaseStock(Integer quantity) {
        this.stockQuantity += quantity;
    }

    public BigDecimal totalPrice(int quantity) {
        return price.multiply(new BigDecimal(quantity));
    }

    public void update(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative : " + price);
        }

        if (stockQuantity != null && stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative : " + stockQuantity);
        }

        this.price = price;
        this.stockQuantity = stockQuantity;

        if (name != null) this.name = name;
        if (productType != null) this.productType = productType;
    }
}