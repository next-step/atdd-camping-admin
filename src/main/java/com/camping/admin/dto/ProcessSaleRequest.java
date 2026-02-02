package com.camping.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ProcessSaleRequest(
        @NotNull @Valid List<SaleItemRequest> items
) {
    public ProcessSaleRequest {
        items = items != null ? items : new ArrayList<>();
    }
}
