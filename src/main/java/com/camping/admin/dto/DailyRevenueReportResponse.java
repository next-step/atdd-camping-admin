package com.camping.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class DailyRevenueReportResponse {
    private final LocalDate date;
    private final BigDecimal totalReservationRevenue;
    private final BigDecimal totalSalesRevenue;
    private final BigDecimal totalRentalRevenue;

    public DailyRevenueReportResponse(LocalDate date, BigDecimal totalReservationRevenue,
                                      BigDecimal totalSalesRevenue, BigDecimal totalRentalRevenue) {
        this.date = date;
        this.totalReservationRevenue = totalReservationRevenue;
        this.totalSalesRevenue = totalSalesRevenue;
        this.totalRentalRevenue = totalRentalRevenue;
    }

    public BigDecimal getGrandTotalRevenue() {
        return totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);
    }
}