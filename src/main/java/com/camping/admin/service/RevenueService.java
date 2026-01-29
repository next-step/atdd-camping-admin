package com.camping.admin.service;

import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.RevenueEntryResponse;
import com.camping.admin.dto.RevenueEntryResponse.EntryType;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.repository.SalesRecordRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        BigDecimal totalSalesRevenue = calculateTotalSalesRevenue(date);
        BigDecimal totalReservationRevenue = calculateTotalReservationRevenue(date);
        BigDecimal totalRentalRevenue = calculateTotalRentalRevenue(date);

        BigDecimal grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new DailyRevenueReportResponse(date, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public RangeRevenueReportResponse generateRangeRevenueReport(LocalDate from, LocalDate to) {
        BigDecimal totalSalesRevenue = calculateTotalSalesRevenue(from, to);
        BigDecimal totalReservationRevenue = calculateTotalReservationRevenue(from, to);
        BigDecimal totalRentalRevenue = calculateTotalRentalRevenue(from, to);

        BigDecimal grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new RangeRevenueReportResponse(from, to, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public List<RevenueEntryResponse> generateRangeRevenueEntries(LocalDate from, LocalDate to) {
        List<RevenueEntryResponse> entries = new ArrayList<>();

        entries.addAll(getSalesRevenueEntries(from, to));
        entries.addAll(getReservationRevenueEntries(from, to));
        entries.addAll(getRentalRevenueEntries(from, to));

        entries.sort(Comparator.comparing(RevenueEntryResponse::getOccurredAt));
        return entries;
    }

    // ================= Private Helper Methods (Sales) =================

    private BigDecimal calculateTotalSalesRevenue(LocalDate date) {
        return salesRecordRepository.findAll().stream()
                .filter(record -> record.getCreatedAt().toLocalDate().equals(date))
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalSalesRevenue(LocalDate from, LocalDate to) {
        return salesRecordRepository.findAll().stream()
                .filter(record -> isDateInRange(record.getCreatedAt().toLocalDate(), from, to))
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<RevenueEntryResponse> getSalesRevenueEntries(LocalDate from, LocalDate to) {
        return salesRecordRepository.findAll().stream()
                .filter(record -> isDateInRange(record.getCreatedAt().toLocalDate(), from, to))
                .map(record -> new RevenueEntryResponse(
                        EntryType.SALE,
                        record.getProduct().getName() + " 외",
                        record.getTotalPrice(),
                        record.getCreatedAt()
                ))
                .toList();
    }

    // ================= Private Helper Methods (Reservation) =================

    private BigDecimal calculateTotalReservationRevenue(LocalDate date) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null && r.getReservationDate().equals(date))
                .map(Reservation::calculateRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalReservationRevenue(LocalDate from, LocalDate to) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> isDateInRange(r.getReservationDate(), from, to))
                .map(Reservation::calculateRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<RevenueEntryResponse> getReservationRevenueEntries(LocalDate from, LocalDate to) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> isDateInRange(r.getReservationDate(), from, to))
                .map(r -> new RevenueEntryResponse(
                        EntryType.RESERVATION,
                        "예약 #" + r.getId(),
                        r.calculateRevenue(),
                        r.getCreatedAt()
                ))
                .toList();
    }

    // ================= Private Helper Methods (Rental) =================

    private BigDecimal calculateTotalRentalRevenue(LocalDate date) {
        return rentalRecordRepository.findAll().stream()
                .filter(rr -> rr.getCreatedAt().toLocalDate().equals(date))
                .map(RentalRecord::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalRentalRevenue(LocalDate from, LocalDate to) {
        return rentalRecordRepository.findAll().stream()
                .filter(rr -> isDateInRange(rr.getCreatedAt().toLocalDate(), from, to))
                .map(RentalRecord::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<RevenueEntryResponse> getRentalRevenueEntries(LocalDate from, LocalDate to) {
        return rentalRecordRepository.findAll().stream()
                .filter(rr -> isDateInRange(rr.getCreatedAt().toLocalDate(), from, to))
                .map(rr -> new RevenueEntryResponse(
                        EntryType.RENTAL,
                        rr.getProduct().getName() + (rr.getReservation() != null ? " (예약#" + rr.getReservation().getId() + ")" : ""),
                        rr.calculateTotalPrice(),
                        rr.getCreatedAt()
                ))
                .toList();
    }

    private boolean isDateInRange(LocalDate date, LocalDate from, LocalDate to) {
        return (date.isEqual(from) || date.isAfter(from)) && (date.isEqual(to) || date.isBefore(to));
    }
}
