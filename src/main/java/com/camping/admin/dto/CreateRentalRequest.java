package com.camping.admin.dto;

public record CreateRentalRequest(
    Long reservationId, // Can be null for walk-ins
    Long productId,
    Integer quantity
) {}
