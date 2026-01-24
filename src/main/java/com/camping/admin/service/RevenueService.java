package com.camping.admin.service;

import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.RevenueEntryResponse;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.repository.SalesRecordRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        BigDecimal totalSalesRevenue = salesRecordRepository.findAll().stream()
                .filter(record -> record.getCreatedAt().toLocalDate().equals(date))
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReservationRevenue = reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null && r.getReservationDate().equals(date))
                .map(r -> {
                    long nights = ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
                    if (nights < 1) nights = 1;
                    return new BigDecimal(nights).multiply(new BigDecimal("50000"));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRentalRevenue = rentalRecordRepository.findAll().stream()
                .filter(rr -> rr.getCreatedAt().toLocalDate().equals(date))
                .map(rr -> rr.getProduct().getPrice().multiply(new BigDecimal(rr.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new DailyRevenueReportResponse(date, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public RangeRevenueReportResponse generateRangeRevenueReport(LocalDate from, LocalDate to) {
        BigDecimal totalSalesRevenue = salesRecordRepository.findAll().stream()
                .filter(record -> isWithinRange(record.getCreatedAt().toLocalDate(), from, to))
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReservationRevenue = reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> isWithinRange(r.getReservationDate(), from, to))
                .map(r -> {
                    long nights = ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
                    if (nights < 1) nights = 1;
                    return new BigDecimal(nights).multiply(new BigDecimal("50000"));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRentalRevenue = rentalRecordRepository.findAll().stream()
                .filter(rr -> isWithinRange(rr.getCreatedAt().toLocalDate(), from, to))
                .map(rr -> rr.getProduct().getPrice().multiply(new BigDecimal(rr.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new RangeRevenueReportResponse(from, to, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public List<RevenueEntryResponse> generateRangeRevenueEntries(LocalDate from, LocalDate to) {
        List<RevenueEntryResponse> entries = new ArrayList<>();

        salesRecordRepository.findAll().stream()
                .filter(record -> isWithinRange(record.getCreatedAt().toLocalDate(), from, to))
                .forEach(record -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.SALE,
                        record.getProduct().getName() + " 외",
                        record.getTotalPrice(),
                        record.getCreatedAt()
                )));

        reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> isWithinRange(r.getReservationDate(), from, to))
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

        rentalRecordRepository.findAll().stream()
                .filter(rr -> isWithinRange(rr.getCreatedAt().toLocalDate(), from, to))
                .forEach(rr -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RENTAL,
                        rr.getProduct().getName() + (rr.getReservation() != null ? " (예약#" + rr.getReservation().getId() + ")" : ""),
                        rr.getProduct().getPrice().multiply(new BigDecimal(rr.getQuantity())),
                        rr.getCreatedAt()
                )));

        entries.sort(Comparator.comparing(RevenueEntryResponse::getOccurredAt));
        return entries;
    }

    private boolean isWithinRange(LocalDate date, LocalDate from, LocalDate to) {
        return (date.isEqual(from) || date.isAfter(from)) && (date.isEqual(to) || date.isBefore(to));
    }
}