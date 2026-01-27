package com.camping.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class RangeRevenueReportResponse {
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final BigDecimal totalReservationRevenue;
    private final BigDecimal totalSalesRevenue;
    private final BigDecimal totalRentalRevenue;

    public RangeRevenueReportResponse(LocalDate fromDate, LocalDate toDate,
                                      BigDecimal totalReservationRevenue,
                                      BigDecimal totalSalesRevenue, BigDecimal totalRentalRevenue) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalReservationRevenue = totalReservationRevenue;
        this.totalSalesRevenue = totalSalesRevenue;
        this.totalRentalRevenue = totalRentalRevenue;
    }

    public BigDecimal getGrandTotalRevenue() {
        return totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);
    }
}