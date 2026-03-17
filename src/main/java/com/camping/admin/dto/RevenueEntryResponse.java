package com.camping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RevenueEntryResponse {

    public enum EntryType {RESERVATION, SALE, RENTAL}

    private EntryType type;
    private String title;
    private BigDecimal amount;
    private LocalDateTime occurredAt;
}


