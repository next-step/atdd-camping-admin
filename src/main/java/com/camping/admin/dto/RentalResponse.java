package com.camping.admin.dto;

import com.camping.admin.domain.entity.RentalRecord;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RentalResponse {
    private final Long id;
    private final Long reservationId;
    private final Long productId;
    private final String productName;
    private final Integer quantity;
    private final Boolean isReturned;
    private final LocalDateTime createdAt;

    public static RentalResponse from(RentalRecord rentalRecord) {
        return new RentalResponse(rentalRecord);
    }

    private RentalResponse(RentalRecord rentalRecord) {
        this.id = rentalRecord.getId();
        this.reservationId = rentalRecord.getReservation() != null ? rentalRecord.getReservation().getId() : null;
        this.productId = rentalRecord.getProduct().getId();
        this.productName = rentalRecord.getProduct().getName();
        this.quantity = rentalRecord.getQuantity();
        this.isReturned = rentalRecord.getIsReturned();
        this.createdAt = rentalRecord.getCreatedAt();
    }
}
