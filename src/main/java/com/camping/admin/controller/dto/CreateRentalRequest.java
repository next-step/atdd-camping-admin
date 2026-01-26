package com.camping.admin.controller.dto;

import com.camping.admin.service.dto.RentalCommand;
import jakarta.annotation.Nullable;
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
    private Integer quantity;

    public CreateRentalRequest(@Nullable Long reservationId, Long productId, Integer quantity) {
        this.reservationId = reservationId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public RentalCommand toServiceDto() {
        return new RentalCommand(reservationId, productId, quantity);
    }
}