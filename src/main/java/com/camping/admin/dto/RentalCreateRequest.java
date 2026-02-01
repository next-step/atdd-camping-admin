package com.camping.admin.dto;

public record RentalCreateRequest(
        Long reservationId,
        Long productId,
        Integer quantity
) {
}