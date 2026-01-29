package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.ProcessSaleRequest;
import com.camping.admin.dto.RevenueEntryResponse;
import com.camping.admin.dto.SaleItemResponse;
import com.camping.admin.dto.SalesRecordResponse;
import com.camping.admin.repository.ProductRepository;
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
public class SalesService {

    private static final BigDecimal NIGHTLY_RATE = new BigDecimal("50000");

    private final ProductRepository productRepository;
    private final SalesRecordRepository salesRecordRepository;
    private final ReservationRepository reservationRepository;
    private final RentalRecordRepository rentalRecordRepository;
    private final ProductService productService;

    @Transactional
    public void processSale(ProcessSaleRequest request) {
        for (SaleItemResponse itemDto : request.getItems()) {
            productService.decreaseStock(itemDto.getProductId(), itemDto.getQuantity());

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + itemDto.getProductId()));

            BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(itemDto.getQuantity()));
            SalesRecord salesRecord = new SalesRecord(product, itemDto.getQuantity(), totalPrice);
            salesRecordRepository.save(salesRecord);
        }
    }

    public List<SalesRecordResponse> findRecentSales(int limit) {
        return salesRecordRepository.findAll().stream()
                .sorted(Comparator.comparing(SalesRecord::getCreatedAt).reversed())
                .limit(limit)
                .map(SalesRecordResponse::from)
                .toList();
    }

    public DailyRevenueReportResponse generateDailyRevenueReport(LocalDate date) {
        BigDecimal salesRevenue = calculateSalesRevenue(date, date);
        BigDecimal reservationRevenue = calculateReservationRevenue(date, date);
        BigDecimal rentalRevenue = calculateRentalRevenue(date, date);
        BigDecimal grandTotal = salesRevenue.add(reservationRevenue).add(rentalRevenue);

        return new DailyRevenueReportResponse(date, reservationRevenue, salesRevenue, rentalRevenue, grandTotal);
    }

    public RangeRevenueReportResponse generateRangeRevenueReport(LocalDate from, LocalDate to) {
        BigDecimal salesRevenue = calculateSalesRevenue(from, to);
        BigDecimal reservationRevenue = calculateReservationRevenue(from, to);
        BigDecimal rentalRevenue = calculateRentalRevenue(from, to);
        BigDecimal grandTotal = salesRevenue.add(reservationRevenue).add(rentalRevenue);

        return new RangeRevenueReportResponse(from, to, reservationRevenue, salesRevenue, rentalRevenue, grandTotal);
    }

    public List<RevenueEntryResponse> generateRangeRevenueEntries(LocalDate from, LocalDate to) {
        List<RevenueEntryResponse> entries = new ArrayList<>();

        addSalesEntries(entries, from, to);
        addReservationEntries(entries, from, to);
        addRentalEntries(entries, from, to);

        entries.sort(Comparator.comparing(RevenueEntryResponse::getOccurredAt));
        return entries;
    }

    // === 내부 헬퍼 메서드 ===

    private boolean isWithinRange(LocalDate target, LocalDate from, LocalDate to) {
        return !target.isBefore(from) && !target.isAfter(to);
    }

    private BigDecimal calculateSalesRevenue(LocalDate from, LocalDate to) {
        return salesRecordRepository.findAll().stream()
                .filter(r -> isWithinRange(r.getCreatedAt().toLocalDate(), from, to))
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateReservationRevenue(LocalDate from, LocalDate to) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> isWithinRange(r.getReservationDate(), from, to))
                .map(this::calculateReservationPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateRentalRevenue(LocalDate from, LocalDate to) {
        return rentalRecordRepository.findAll().stream()
                .filter(rr -> isWithinRange(rr.getCreatedAt().toLocalDate(), from, to))
                .map(this::calculateRentalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateReservationPrice(Reservation reservation) {
        long nights = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        if (nights < 1) nights = 1;
        return new BigDecimal(nights).multiply(NIGHTLY_RATE);
    }

    private BigDecimal calculateRentalPrice(RentalRecord rental) {
        return rental.getProduct().getPrice().multiply(new BigDecimal(rental.getQuantity()));
    }

    private void addSalesEntries(List<RevenueEntryResponse> entries, LocalDate from, LocalDate to) {
        salesRecordRepository.findAll().stream()
                .filter(r -> isWithinRange(r.getCreatedAt().toLocalDate(), from, to))
                .forEach(record -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.SALE,
                        record.getProduct().getName() + " 외",
                        record.getTotalPrice(),
                        record.getCreatedAt()
                )));
    }

    private void addReservationEntries(List<RevenueEntryResponse> entries, LocalDate from, LocalDate to) {
        reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> isWithinRange(r.getReservationDate(), from, to))
                .forEach(r -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RESERVATION,
                        "예약 #" + r.getId(),
                        calculateReservationPrice(r),
                        r.getCreatedAt()
                )));
    }

    private void addRentalEntries(List<RevenueEntryResponse> entries, LocalDate from, LocalDate to) {
        rentalRecordRepository.findAll().stream()
                .filter(rr -> isWithinRange(rr.getCreatedAt().toLocalDate(), from, to))
                .forEach(rr -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RENTAL,
                        rr.getProduct().getName() + (rr.getReservation() != null ? " (예약#" + rr.getReservation().getId() + ")" : ""),
                        calculateRentalPrice(rr),
                        rr.getCreatedAt()
                )));
    }
}
