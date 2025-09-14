package com.camping.admin.domain.event;

import com.camping.admin.domain.vo.RecordQuantity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductStockIncreasedEvent implements DomainEvent {
    private final Long productId;
    private final RecordQuantity quantity;
    private final LocalDateTime occurredOn;

    public ProductStockIncreasedEvent(Long productId, RecordQuantity quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.occurredOn = LocalDateTime.now();
    }
}