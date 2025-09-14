package com.camping.admin.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductStockDecreasedEvent implements DomainEvent {
    private final Long productId;
    private final Integer quantity;
    private final LocalDateTime occurredOn;

    public ProductStockDecreasedEvent(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.occurredOn = LocalDateTime.now();
    }
}