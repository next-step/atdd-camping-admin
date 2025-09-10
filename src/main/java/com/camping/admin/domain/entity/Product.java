package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.exception.ValidationException;
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
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }

    public void decreaseStock(Integer quantity) {
        validateQuantity(quantity);
        if (this.stockQuantity < quantity) {
            throw new ValidationException("Not enough stock for product " + this.name);
        }
        this.stockQuantity = this.stockQuantity - quantity;
    }

    public void increaseStock(Integer quantity) {
        validateQuantity(quantity);
        this.stockQuantity = this.stockQuantity + quantity;
    }

    public boolean isRentalType() {
        return ProductType.RENTAL.equals(this.productType);
    }

    public BigDecimal calculateTotalPrice(Integer quantity) {
        validateQuantity(quantity);
        return this.price.multiply(new BigDecimal(quantity));
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("Quantity must be greater than 0");
        }
    }

}
