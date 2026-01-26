package com.camping.admin.domain.entity;

import com.camping.admin.domain.vo.TotalAmount;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price", nullable = false))
    private TotalAmount totalAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public SalesRecord(Product product, Integer quantity) {
        product.validateSellable();
        product.decreaseStock(quantity);

        this.product = product;
        this.quantity = quantity;
        this.totalAmount = new TotalAmount(product.getPrice(), quantity);
        this.createdAt = LocalDateTime.now();
    }

    public BigDecimal getTotalPrice() {
        return totalAmount.getValue();
    }
}