package com.camping.admin.domain.entity;

import com.camping.admin.domain.event.ProductStockDecreasedEvent;
import com.camping.admin.domain.event.ProductStockIncreasedEvent;
import com.camping.admin.domain.vo.RecordQuantity;
import com.camping.admin.exception.RentalAlreadyReturnedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    @Embedded
    private RecordQuantity quantity;

    @Column(name = "is_returned", nullable = false)
    private Boolean isReturned = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RentalRecord(Reservation reservation, Product product, Integer quantity) {
        this.reservation = reservation;
        this.product = product;
        this.quantity = new RecordQuantity(quantity);
        this.isReturned = false;
        this.createdAt = LocalDateTime.now();

        registerEvent(new ProductStockDecreasedEvent(product.getId(), quantity));
    }

    public void returnProduct() {
        if (isReturned) {
            throw new RentalAlreadyReturnedException("This item has already been returned.");
        }
        this.isReturned = true;

        registerEvent(new ProductStockIncreasedEvent(product.getId(), quantity.getQuantity()));
    }

    public Collection<Object> getDomainEvents() {
        return domainEvents();
    }

    public void clearEvents() {
        clearDomainEvents();
    }

    public List<Object> getDomainEventsList() {
        return new ArrayList<>(domainEvents());
    }
}
