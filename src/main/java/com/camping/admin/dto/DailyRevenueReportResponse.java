package com.camping.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyRevenueReportResponse(
    LocalDate date,
    BigDecimal totalReservationRevenue,
    BigDecimal totalSalesRevenue,
    BigDecimal totalRentalRevenue,
    BigDecimal grandTotalRevenue
) {}
