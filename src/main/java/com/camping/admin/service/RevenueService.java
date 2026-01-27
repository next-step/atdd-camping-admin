package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.vo.DateTimeRange;
import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.RevenueEntryResponse;
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
        DateTimeRange range = DateTimeRange.ofDay(date);

        BigDecimal totalSalesRevenue = salesRecordRepository.sumTotalPriceByCreatedAtBetween(range.getStart(), range.getEnd());

        BigDecimal totalReservationRevenue = reservationRepository.findByReservationDate(date).stream()
                .map(Reservation::calculateRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRentalRevenue = rentalRecordRepository.sumRevenueByCreatedAtBetween(range.getStart(), range.getEnd());

        return new DailyRevenueReportResponse(date, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue);
    }

    public RangeRevenueReportResponse generateRangeRevenueReport(LocalDate from, LocalDate to) {
        DateTimeRange range = DateTimeRange.ofRange(from, to);

        BigDecimal totalSalesRevenue = salesRecordRepository.sumTotalPriceByCreatedAtBetween(range.getStart(), range.getEnd());

        BigDecimal totalReservationRevenue = reservationRepository.findByReservationDateBetween(from, to).stream()
                .map(Reservation::calculateRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRentalRevenue = rentalRecordRepository.sumRevenueByCreatedAtBetween(range.getStart(), range.getEnd());

        return new RangeRevenueReportResponse(from, to, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue);
    }

    public List<RevenueEntryResponse> generateRangeRevenueEntries(LocalDate from, LocalDate to) {
        DateTimeRange range = DateTimeRange.ofRange(from, to);

        List<RevenueEntryResponse> entries = new ArrayList<>();

        salesRecordRepository.findByCreatedAtBetween(range.getStart(), range.getEnd())
                .forEach(record -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.SALE,
                        record.getProduct().getName() + " 외",
                        record.getTotalPrice(),
                        record.getCreatedAt()
                )));

        reservationRepository.findByReservationDateBetween(from, to)
                .forEach(r -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RESERVATION,
                        "예약 #" + r.getId(),
                        r.calculateRevenue(),
                        r.getCreatedAt()
                )));

        rentalRecordRepository.findByCreatedAtBetween(range.getStart(), range.getEnd())
                .forEach(rr -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RENTAL,
                        rr.getProduct().getName() + (rr.getReservation() != null ? " (예약#" + rr.getReservation().getId() + ")" : ""),
                        rr.calculateRevenue(),
                        rr.getCreatedAt()
                )));

        entries.sort(Comparator.comparing(RevenueEntryResponse::getOccurredAt));
        return entries;
    }
}