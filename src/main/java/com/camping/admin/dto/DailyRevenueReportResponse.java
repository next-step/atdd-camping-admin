package com.camping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailyRevenueReportResponse {
    private LocalDate date;
    private BigDecimal totalReservationRevenue;
    private BigDecimal totalSalesRevenue;
    private BigDecimal totalRentalRevenue;
    private BigDecimal grandTotalRevenue;
}
