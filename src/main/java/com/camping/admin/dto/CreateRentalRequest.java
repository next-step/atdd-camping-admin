package com.camping.admin.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRentalRequest {
    @Nullable
    private Long reservationId; // Can be null for walk-ins

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}