package com.camping.admin.service;

import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.RevenueEntryResponse;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.repository.SalesRecordRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RevenueService {

    private final SalesRecordRepository salesRecordRepository;
    private final ReservationRepository reservationRepository;
    private final RentalRecordRepository rentalRecordRepository;

    public DailyRevenueReportResponse generateDailyRevenueReport(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        BigDecimal totalSalesRevenue = salesRecordRepository.sumTotalPriceByCreatedAtBetween(startOfDay, endOfDay);

        BigDecimal totalReservationRevenue = reservationRepository.findByReservationDate(date).stream()
                .map(r -> {
                    long nights = ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
                    if (nights < 1) nights = 1;
                    return new BigDecimal(nights).multiply(new BigDecimal("50000"));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRentalRevenue = rentalRecordRepository.sumRevenueByCreatedAtBetween(startOfDay, endOfDay);

        BigDecimal grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new DailyRevenueReportResponse(date, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public RangeRevenueReportResponse generateRangeRevenueReport(LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = from.atStartOfDay();
        LocalDateTime endDateTime = to.plusDays(1).atStartOfDay();

        BigDecimal totalSalesRevenue = salesRecordRepository.sumTotalPriceByCreatedAtBetween(startDateTime, endDateTime);

        BigDecimal totalReservationRevenue = reservationRepository.findByReservationDateBetween(from, to).stream()
                .map(r -> {
                    long nights = ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
                    if (nights < 1) nights = 1;
                    return new BigDecimal(nights).multiply(new BigDecimal("50000"));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRentalRevenue = rentalRecordRepository.sumRevenueByCreatedAtBetween(startDateTime, endDateTime);

        BigDecimal grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new RangeRevenueReportResponse(from, to, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public List<RevenueEntryResponse> generateRangeRevenueEntries(LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = from.atStartOfDay();
        LocalDateTime endDateTime = to.plusDays(1).atStartOfDay();

        List<RevenueEntryResponse> entries = new ArrayList<>();

        salesRecordRepository.findByCreatedAtBetween(startDateTime, endDateTime)
                .forEach(record -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.SALE,
                        record.getProduct().getName() + " 외",
                        record.getTotalPrice(),
                        record.getCreatedAt()
                )));

        reservationRepository.findByReservationDateBetween(from, to)
                .forEach(r -> {
                    long nights = ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
                    if (nights < 1) nights = 1;
                    entries.add(new RevenueEntryResponse(
                            RevenueEntryResponse.EntryType.RESERVATION,
                            "예약 #" + r.getId(),
                            new BigDecimal(nights).multiply(new BigDecimal("50000")),
                            r.getCreatedAt()
                    ));
                });

        rentalRecordRepository.findByCreatedAtBetween(startDateTime, endDateTime)
                .forEach(rr -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RENTAL,
                        rr.getProduct().getName() + (rr.getReservation() != null ? " (예약#" + rr.getReservation().getId() + ")" : ""),
                        rr.getProduct().getPrice().multiply(new BigDecimal(rr.getQuantity())),
                        rr.getCreatedAt()
                )));

        entries.sort(Comparator.comparing(RevenueEntryResponse::getOccurredAt));
        return entries;
    }
}