package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.domain.vo.RecordQuantity;
import com.camping.admin.domain.vo.StockQuantity;
import com.camping.admin.exception.ProductNotRentalException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    @Embedded
    @AttributeOverride(name = "quantity", column = @Column(name = "stock_quantity", nullable = false))
    private StockQuantity stockQuantity;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    public Product(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stockQuantity = new StockQuantity(stockQuantity);
        this.price = price;
        this.productType = productType;
    }

    public void decreaseStock(RecordQuantity quantity) {
        validateRentalProduct();
        this.stockQuantity = this.stockQuantity.decrease(quantity);
    }

    public void increaseStock(RecordQuantity quantity) {
        this.stockQuantity = this.stockQuantity.increase(quantity);
    }

    private void validateRentalProduct() {
        if (productType != ProductType.RENTAL) {
            throw new ProductNotRentalException("Product is not a rental item.");
        }
    }
}