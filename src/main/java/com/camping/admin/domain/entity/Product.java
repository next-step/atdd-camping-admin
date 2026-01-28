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

    public void update(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name != null ? name : this.name;
        this.stockQuantity = stockQuantity != null ? stockQuantity : this.stockQuantity;
        this.price = price != null ? price : this.price;
        this.productType = productType != null ? productType : this.productType;
    }

    public void decreaseStock(Integer quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("재고가 부족합니다");
        }
        this.stockQuantity -= quantity;
    }

    public void increaseStock(Integer quantity) {
        this.stockQuantity += quantity;
    }
}