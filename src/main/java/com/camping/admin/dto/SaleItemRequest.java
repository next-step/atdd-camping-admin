package com.camping.admin.dto;

import jakarta.validation.constraints.NotNull;

public record SaleItemRequest(
        @NotNull Long productId,
        @NotNull Integer quantity
) {
}
