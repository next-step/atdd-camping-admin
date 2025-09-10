package com.camping.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RevenueEntryResponse(
    EntryType type,
    String title,
    BigDecimal amount,
    LocalDateTime occurredAt
) {
    public enum EntryType { RESERVATION, SALE, RENTAL }
}
