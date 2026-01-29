package com.camping.admin.domain.entity;

import com.camping.admin.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "rental_records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id") // Nullable for walk-in rentals
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "is_returned", nullable = false)
    private Boolean isReturned = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RentalRecord(Reservation reservation, Product product, Integer quantity) {
        this.reservation = reservation;
        this.product = product;
        this.quantity = quantity;
        this.isReturned = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 반납 처리 (검증 포함)
     */
    public void markAsReturned() {
        if (this.isReturned) {
            throw new BusinessException("이미 반납된 상품입니다.", HttpStatus.CONFLICT);
        }
        this.isReturned = true;
    }

    /**
     * 대여 총 가격 계산
     */
    public BigDecimal calculateTotalPrice() {
        return this.product.calculatePrice(this.quantity);
    }
}
