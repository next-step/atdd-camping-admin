package com.camping.admin.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRentalRequest {
    @Nullable
    private Long reservationId; // Can be null for walk-ins
    private Long productId;
    private Integer quantity;
}