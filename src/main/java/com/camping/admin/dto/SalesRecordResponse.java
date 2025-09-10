package com.camping.admin.dto;

import com.camping.admin.domain.entity.SalesRecord;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class SalesRecordResponse {
    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    private SalesRecordResponse(SalesRecord record) {
        this.id = record.getId();
        this.productName = record.getProduct().getName();
        this.quantity = record.getQuantity();
        this.totalPrice = record.getTotalPrice();
        this.createdAt = record.getCreatedAt();
    }

    public static SalesRecordResponse from(SalesRecord record) {
        return new SalesRecordResponse(record);
    }
}
