package com.camping.admin.dto;

import com.camping.admin.domain.entity.SalesRecord;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class SalesRecordResponse {
    private final Long id;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal totalPrice;
    private final LocalDateTime createdAt;

    public static SalesRecordResponse from(SalesRecord record) {
        return new SalesRecordResponse(record);
    }

    private SalesRecordResponse(SalesRecord record) {
        this.id = record.getId();
        this.productName = record.getProduct().getName();
        this.quantity = record.getQuantity();
        this.totalPrice = record.getTotalPrice();
        this.createdAt = record.getCreatedAt();
    }
}


