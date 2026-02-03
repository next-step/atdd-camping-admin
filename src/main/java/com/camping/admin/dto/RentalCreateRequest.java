package com.camping.admin.dto;

import jakarta.validation.constraints.NotNull;

public record RentalCreateRequest(
        Long reservationId,
        @NotNull Long productId,
        @NotNull Integer quantity
) {
}