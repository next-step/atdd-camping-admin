package com.camping.admin.domain.entity;

import com.camping.admin.domain.event.ProductStockDecreasedEvent;
import com.camping.admin.domain.event.ProductStockIncreasedEvent;
import com.camping.admin.exception.RentalAlreadyReturnedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "rental_records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalRecord extends AbstractAggregateRoot<RentalRecord> {

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

        registerEvent(new ProductStockDecreasedEvent(product.getId(), quantity));
    }

    public void returnProduct() {
        if (isReturned) {
            throw new RentalAlreadyReturnedException("This item has already been returned.");
        }
        this.isReturned = true;

        registerEvent(new ProductStockIncreasedEvent(product.getId(), quantity));
    }

    // 테스트를 위한 도메인 이벤트 접근 메서드
    public java.util.Collection<Object> getDomainEvents() {
        return domainEvents();
    }

    // 테스트를 위한 도메인 이벤트 초기화 메서드
    public void clearEvents() {
        clearDomainEvents();
    }

    // 테스트를 위한 도메인 이벤트 리스트 접근 메서드
    public java.util.List<Object> getDomainEventsList() {
        return new java.util.ArrayList<>(domainEvents());
    }
}
