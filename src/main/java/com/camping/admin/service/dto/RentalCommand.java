package com.camping.admin.service.dto;

import com.google.common.base.Preconditions;
import jakarta.annotation.Nullable;


public record RentalCommand(@Nullable Long reservationId, Long productId, Integer quantity) {
    public RentalCommand {
        Preconditions.checkNotNull(productId, "Product ID cannot be null");
        Preconditions.checkNotNull(quantity, "Quantity must be greater than zero.");
        Preconditions.checkArgument(quantity > 0, "Quantity must be greater than zero");

    }
}
