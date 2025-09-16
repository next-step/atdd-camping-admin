package com.camping.admin.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductStockIncreasedEvent implements DomainEvent {
    private final Long productId;
    private final Integer quantity; // Best Practice: 원시 타입 사용 (직렬화 용이성)
    private final LocalDateTime occurredOn;

    public ProductStockIncreasedEvent(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.occurredOn = LocalDateTime.now();
    }
}