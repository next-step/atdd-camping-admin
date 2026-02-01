package com.camping.admin.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "rental_records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static RentalRecord create(Reservation reservation, Product product, Integer quantity, LocalDateTime now) {
        var rentalRecord = new RentalRecord();

        rentalRecord.reservation = reservation;
        rentalRecord.product = product;
        rentalRecord.quantity = quantity;
        rentalRecord.isReturned = false;
        rentalRecord.createdAt = now;

        return rentalRecord;
    }

    public void returnItem() {
        if (isReturned) {
            throw new IllegalStateException("This item has already been returned.");
        }
        isReturned = true;
        product.increaseStock(quantity);
    }
}
