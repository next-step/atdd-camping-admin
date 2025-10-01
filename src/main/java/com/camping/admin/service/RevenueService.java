package com.camping.admin.service;

import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.RevenueEntryResponse;
import com.camping.admin.domain.constants.BusinessConstants;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.repository.SalesRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RevenueService {

    private final SalesRecordRepository salesRecordRepository;
    private final ReservationRepository reservationRepository;
    private final RentalRecordRepository rentalRecordRepository;

    public DailyRevenueReportResponse generateDailyRevenueReport(LocalDate date) {
        BigDecimal totalSalesRevenue = calculateDailySalesRevenue(date);
        BigDecimal totalReservationRevenue = calculateDailyReservationRevenue(date);
        BigDecimal totalRentalRevenue = calculateDailyRentalRevenue(date);
        BigDecimal grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new DailyRevenueReportResponse(date, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public RangeRevenueReportResponse generateRangeRevenueReport(LocalDate from, LocalDate to) {
        BigDecimal totalSalesRevenue = calculateRangeSalesRevenue(from, to);
        BigDecimal totalReservationRevenue = calculateRangeReservationRevenue(from, to);
        BigDecimal totalRentalRevenue = calculateRangeRentalRevenue(from, to);
        BigDecimal grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new RangeRevenueReportResponse(from, to, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public List<RevenueEntryResponse> generateRangeRevenueEntries(LocalDate from, LocalDate to) {
        List<RevenueEntryResponse> entries = new ArrayList<>();

        addSalesEntries(entries, from, to);
        addReservationEntries(entries, from, to);
        addRentalEntries(entries, from, to);

        entries.sort(Comparator.comparing(RevenueEntryResponse::getOccurredAt));
        return entries;
    }

    private BigDecimal calculateDailySalesRevenue(LocalDate date) {
        return salesRecordRepository.findByCreatedAtDate(date).stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDailyReservationRevenue(LocalDate date) {
        return reservationRepository.findByReservationDate(date).stream()
                .map(Reservation::calculateRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDailyRentalRevenue(LocalDate date) {
        return rentalRecordRepository.findByCreatedAtDate(date).stream()
                .map(it -> it.getProduct().getPrice().multiply(new BigDecimal(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateRangeSalesRevenue(LocalDate from, LocalDate to) {
        return salesRecordRepository.findByCreatedAtDateBetween(from, to).stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateRangeReservationRevenue(LocalDate from, LocalDate to) {
        return reservationRepository.findByReservationDateBetween(from, to).stream()
                .map(Reservation::calculateRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateRangeRentalRevenue(LocalDate from, LocalDate to) {
        return rentalRecordRepository.findByCreatedAtDateBetween(from, to).stream()
                .map(rr -> rr.getProduct().getPrice().multiply(new BigDecimal(rr.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void addSalesEntries(List<RevenueEntryResponse> entries, LocalDate from, LocalDate to) {
        salesRecordRepository.findByCreatedAtDateBetween(from, to)
                .forEach(record -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.SALE,
                        record.getProduct().getName() + BusinessConstants.SALES_ITEM_SUFFIX,
                        record.getTotalPrice(),
                        record.getCreatedAt()
                )));
    }

    private void addReservationEntries(List<RevenueEntryResponse> entries, LocalDate from, LocalDate to) {
        reservationRepository.findByReservationDateBetween(from, to)
                .forEach(it -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RESERVATION,
                        BusinessConstants.RESERVATION_PREFIX + it.getId(),
                        it.calculateRevenue(),
                        it.getCreatedAt()
                )));
    }

    private void addRentalEntries(List<RevenueEntryResponse> entries, LocalDate from, LocalDate to) {
        rentalRecordRepository.findByCreatedAtDateBetween(from, to)
                .forEach(it -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RENTAL,
                        it.getProduct().getName() + (it.getReservation() != null ?
                            BusinessConstants.RENTAL_RESERVATION_SUFFIX + it.getReservation().getId() + BusinessConstants.PARENTHESIS_CLOSE : ""),
                        it.getProduct().getPrice().multiply(new BigDecimal(it.getQuantity())),
                        it.getCreatedAt()
                )));
    }
}
