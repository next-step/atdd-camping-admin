package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
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
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.price = price != null ? price : BigDecimal.ZERO;
        this.productType = productType != null ? productType : ProductType.SALE;
    }

    public void update(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        if (name != null) {
            this.name = name;
        }
        if (stockQuantity != null) {
            this.stockQuantity = stockQuantity;
        }
        if (price != null) {
            this.price = price;
        }
        if (productType != null) {
            this.productType = productType;
        }
    }

    public boolean isRentable() {
        return this.productType == ProductType.RENTAL;
    }

    public void validateRentable() {
        if (!isRentable()) {
            throw new IllegalStateException("대여 불가능한 상품입니다");
        }
    }

    public boolean isSellable() {
        return this.productType == ProductType.SALE;
    }

    public void validateSellable() {
        if (!isSellable()) {
            throw new IllegalStateException("판매 불가능한 상품입니다");
        }
    }
  public void decreaseStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Not enough stock for product " + this.name);
        }
        this.stockQuantity -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }
}