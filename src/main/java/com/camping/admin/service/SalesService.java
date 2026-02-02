package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.ProcessSaleRequest;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.RevenueEntryResponse;
import com.camping.admin.dto.SalesRecordResponse;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.repository.SalesRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesService {

    private static final BigDecimal NIGHTLY_RATE = new BigDecimal("50000");

    private final ProductRepository productRepository;
    private final SalesRecordRepository salesRecordRepository;
    private final ReservationRepository reservationRepository;
    private final RentalRecordRepository rentalRecordRepository;

    // ===== Command =====

    @Transactional
    public void processSale(ProcessSaleRequest request) {
        for (var item : request.items()) {
            var product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + item.productId()));

            product.decreaseStock(item.quantity());

            var salesRecord = SalesRecord.create(product, item.quantity());
            salesRecordRepository.save(salesRecord);
        }
    }

    // ===== Query =====

    public List<SalesRecordResponse> findRecentSales(int limit) {
        return salesRecordRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit)).stream()
                .map(SalesRecordResponse::from)
                .toList();
    }

    public DailyRevenueReportResponse generateDailyRevenueReport(LocalDate date) {
        var totalSalesRevenue = salesRecordRepository.findByCreatedDate(date).stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalReservationRevenue = reservationRepository.findByReservationDate(date).stream()
                .map(this::calculateReservationRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalRentalRevenue = rentalRecordRepository.findByCreatedDateWithProduct(date).stream()
                .map(rr -> rr.getProduct().calculateTotalPrice(rr.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new DailyRevenueReportResponse(date, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public RangeRevenueReportResponse generateRangeRevenueReport(LocalDate from, LocalDate to) {
        var totalSalesRevenue = salesRecordRepository.findByCreatedDateBetween(from, to).stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalReservationRevenue = reservationRepository.findByReservationDateBetween(from, to).stream()
                .map(this::calculateReservationRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalRentalRevenue = rentalRecordRepository.findByCreatedDateWithProductBetween(from, to).stream()
                .map(rr -> rr.getProduct().calculateTotalPrice(rr.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new RangeRevenueReportResponse(from, to, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    public List<RevenueEntryResponse> generateRangeRevenueEntries(LocalDate from, LocalDate to) {
        var entries = new ArrayList<RevenueEntryResponse>();

        salesRecordRepository.findByCreatedDateBetween(from, to)
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
                        calculateReservationRevenue(r),
                        r.getCreatedAt()
                )));

        rentalRecordRepository.findByCreatedDateWithProductBetween(from, to)
                .forEach(rr -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RENTAL,
                        rr.getProduct().getName() + (rr.getReservation() != null ? " (예약#" + rr.getReservation().getId() + ")" : ""),
                        rr.getProduct().calculateTotalPrice(rr.getQuantity()),
                        rr.getCreatedAt()
                )));

        entries.sort(Comparator.comparing(RevenueEntryResponse::getOccurredAt));
        return entries;
    }

    // ===== Helper =====

    private BigDecimal calculateReservationRevenue(Reservation reservation) {
        var nights = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        if (nights < 1) {
            nights = 1;
        }
        return NIGHTLY_RATE.multiply(BigDecimal.valueOf(nights));
    }
}
