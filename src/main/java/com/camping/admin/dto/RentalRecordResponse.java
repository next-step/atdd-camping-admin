package com.camping.admin.dto;

import com.camping.admin.domain.entity.RentalRecord;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;

public record RentalRecordResponse(
        Long id,
        Long reservationId,
        Long productId,
        String productName,
        Integer quantity,
        Boolean isReturned,
        LocalDateTime createdAt
) {
    public static RentalRecordResponse from(RentalRecord rentalRecord) {
        return new RentalRecordResponse(
                rentalRecord.getId(),
                rentalRecord.getReservation() != null ? rentalRecord.getReservation().getId() : null,
                rentalRecord.getProduct().getId(),
                rentalRecord.getProduct().getName(),
                rentalRecord.getQuantity(),
                rentalRecord.getIsReturned(),
                rentalRecord.getCreatedAt());
    }
}