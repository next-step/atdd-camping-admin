package com.camping.admin.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "sales_records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalesRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public SalesRecord(Product product, Integer quantity, BigDecimal totalPrice) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.createdAt = LocalDateTime.now();
    }

    public static SalesRecord createFromProduct(Product product, Integer quantity) {
        BigDecimal totalPrice = product.calculateTotalPrice(quantity);
        return new SalesRecord(product, quantity, totalPrice);
    }

    public boolean isInDateRange(LocalDate from, LocalDate to) {
        LocalDate date = this.createdAt.toLocalDate();
        return (date.isEqual(from) || date.isAfter(from)) && (date.isEqual(to) || date.isBefore(to));
    }

    public boolean isOnDate(LocalDate date) {
        return this.createdAt.toLocalDate().equals(date);
    }
}
