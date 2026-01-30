package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.exception.BusinessException;
import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.*;
import org.springframework.http.HttpStatus;

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

    @Builder
    public Product(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }

    /**
     * 대여 가능 여부 검증
     */
    public void validateRentable() {
        if (this.productType != ProductType.RENTAL) {
            throw new BusinessException("대여 불가 상품입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 재고 감소 (검증 포함)
     */
    public void decreaseStock(Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException("수량은 1개 이상이여햐 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (this.stockQuantity < quantity) {
            throw new BusinessException("재고가 부족합니다: " + this.name, HttpStatus.CONFLICT);
        }
        this.stockQuantity -= quantity;
    }

    /**
     * 재고 증가
     */
    public void increaseStock(Integer quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 가격 계산
     */
    public BigDecimal calculatePrice(Integer quantity) {
        return this.price.multiply(new BigDecimal(quantity));
    }

    /**
     * 상품 정보 업데이트
     */
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
}