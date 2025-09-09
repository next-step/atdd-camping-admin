package com.camping.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RangeRevenueReportResponse(
    LocalDate fromDate,
    LocalDate toDate,
    BigDecimal totalReservationRevenue,
    BigDecimal totalSalesRevenue,
    BigDecimal totalRentalRevenue,
    BigDecimal grandTotalRevenue
) {}


