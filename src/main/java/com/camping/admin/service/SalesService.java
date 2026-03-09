package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.ProcessSaleRequest;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.RevenueEntryResponse;
import com.camping.admin.dto.SaleItemResponse;
import com.camping.admin.dto.SalesRecordResponse;
import com.camping.admin.repository.ProductRepository;
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
public class SalesService {

    private final ProductRepository productRepository;
    private final SalesRecordRepository salesRecordRepository;
    private final ReservationRepository reservationRepository; // For reports
    private final RentalRecordRepository rentalRecordRepository; // For reports
    private final ProductService productService; // To use decreaseStock

    @Transactional
    public void processSale(ProcessSaleRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("판매 항목은 최소 1개 이상이어야 합니다.");
        }
        for (SaleItemResponse itemDto : request.getItems()) {
            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("판매 수량은 1 이상이어야 합니다.");
            }
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("판매 요청에 존재하지 않는 상품이 포함되어 있습니다."));

            productService.decreaseStock(itemDto.getProductId(), itemDto.getQuantity());

            BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(itemDto.getQuantity()));

            SalesRecord salesRecord = new SalesRecord(product, itemDto.getQuantity(), totalPrice);
            salesRecordRepository.save(salesRecord);
        }
    }

    public List<SalesRecordResponse> findRecentSales(int limit) {
        return salesRecordRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(com.camping.admin.domain.entity.SalesRecord::getCreatedAt).reversed())
                .limit(limit)
                .map(SalesRecordResponse::from)
                .toList();
    }

    public DailyRevenueReportResponse generateDailyRevenueReport(LocalDate date) {
        BigDecimal totalSalesRevenue = salesRecordRepository.findAll().stream()
                .filter(record -> record.getCreatedAt().toLocalDate().equals(date))
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReservationRevenue = reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null && r.getReservationDate().equals(date))
                .map(r -> {
                    long nights = java.time.temporal.ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
                    if (nights < 1) nights = 1; // 최소 1박 처리
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
        validateDateRange(from, to);
        BigDecimal totalSalesRevenue = salesRecordRepository.findAll().stream()
                .filter(record -> {
                    LocalDate d = record.getCreatedAt().toLocalDate();
                    return (d.isEqual(from) || d.isAfter(from)) && (d.isEqual(to) || d.isBefore(to));
                })
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReservationRevenue = reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> {
                    LocalDate d = r.getReservationDate();
                    return (d.isEqual(from) || d.isAfter(from)) && (d.isEqual(to) || d.isBefore(to));
                })
                .map(r -> {
                    long nights = java.time.temporal.ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
                    if (nights < 1) nights = 1;
                    return new BigDecimal(nights).multiply(new BigDecimal("50000"));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRentalRevenue = rentalRecordRepository.findAll().stream()
                .filter(rr -> {
                    LocalDate d = rr.getCreatedAt().toLocalDate();
                    return (d.isEqual(from) || d.isAfter(from)) && (d.isEqual(to) || d.isBefore(to));
                })
                .map(rr -> rr.getProduct().getPrice().multiply(new BigDecimal(rr.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandTotal = totalSalesRevenue.add(totalReservationRevenue).add(totalRentalRevenue);

        return new RangeRevenueReportResponse(from, to, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦을 수 없습니다.");
        }
    }

    public List<RevenueEntryResponse> generateRangeRevenueEntries(LocalDate from, LocalDate to) {
        List<RevenueEntryResponse> entries = new ArrayList<>();

        salesRecordRepository.findAll().stream()
                .filter(record -> {
                    LocalDate d = record.getCreatedAt().toLocalDate();
                    return (d.isEqual(from) || d.isAfter(from)) && (d.isEqual(to) || d.isBefore(to));
                })
                .forEach(record -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.SALE,
                        record.getProduct().getName() + " 외",
                        record.getTotalPrice(),
                        record.getCreatedAt()
                )));

        reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> {
                    LocalDate d = r.getReservationDate();
                    return (d.isEqual(from) || d.isAfter(from)) && (d.isEqual(to) || d.isBefore(to));
                })
                .forEach(r -> {
                    long nights = java.time.temporal.ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
                    if (nights < 1) nights = 1;
                    entries.add(new RevenueEntryResponse(
                            RevenueEntryResponse.EntryType.RESERVATION,
                            "예약 #" + r.getId(),
                            new java.math.BigDecimal(nights).multiply(new java.math.BigDecimal("50000")),
                            r.getCreatedAt()
                    ));
                });

        rentalRecordRepository.findAll().stream()
                .filter(rr -> {
                    LocalDate d = rr.getCreatedAt().toLocalDate();
                    return (d.isEqual(from) || d.isAfter(from)) && (d.isEqual(to) || d.isBefore(to));
                })
                .forEach(rr -> entries.add(new RevenueEntryResponse(
                        RevenueEntryResponse.EntryType.RENTAL,
                        rr.getProduct().getName() + (rr.getReservation() != null ? " (예약#" + rr.getReservation().getId() + ")" : ""),
                        rr.getProduct().getPrice().multiply(new java.math.BigDecimal(rr.getQuantity())),
                        rr.getCreatedAt()
                )));

        entries.sort(Comparator.comparing(RevenueEntryResponse::getOccurredAt));
        return entries;
    }
}
