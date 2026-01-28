package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.domain.exception.ProductErrorCode;
import com.camping.admin.domain.vo.Stock;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @Embedded
    @AttributeOverride(name = "quantity", column = @Column(name = "stock_quantity", nullable = false))
    private Stock stock;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    public Product(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stock = stockQuantity != null ? new Stock(stockQuantity) : Stock.zero();
        this.price = price != null ? price : BigDecimal.ZERO;
        this.productType = productType != null ? productType : ProductType.SALE;
    }

    public void update(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        if (name != null) {
            this.name = name;
        }
        if (stockQuantity != null) {
            this.stock = new Stock(stockQuantity);
        }
        if (price != null) {
            this.price = price;
        }
        if (productType != null) {
            this.productType = productType;
        }
    }

    public boolean isRentable() {
        return this.productType.isRentable();
    }

    public void validateRentable() {
        if (!isRentable()) {
            throw ProductErrorCode.NOT_RENTABLE.toException();
        }
    }

    public boolean isSellable() {
        return this.productType.isSellable();
    }

    public void validateSellable() {
        if (!isSellable()) {
            throw ProductErrorCode.NOT_SELLABLE.toException();
        }
    }

    public void decreaseStock(int quantity) {
        this.stock = this.stock.decrease(quantity);
    }

    public void increaseStock(int quantity) {
        this.stock = this.stock.increase(quantity);
    }

    // 기존 API 호환을 위한 위임 메서드
    public Integer getStockQuantity() {
        return stock != null ? stock.getQuantity() : 0;
    }
}