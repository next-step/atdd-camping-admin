package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
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
import com.camping.admin.exception.EntityNotFoundException;
import com.camping.admin.exception.SalesException;
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
public class SalesService {

    private final ProductRepository productRepository;
    private final SalesRecordRepository salesRecordRepository;
    private final ReservationRepository reservationRepository; // For reports
    private final RentalRecordRepository rentalRecordRepository; // For reports
    private final ProductService productService; // To use decreaseStock

    @Transactional
    public void processSale(ProcessSaleRequest request) {
        validateSaleRequest(request);
        processSaleItems(request.items());
    }

    private void validateSaleRequest(ProcessSaleRequest request) {
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new SalesException("Sale request cannot be empty");
        }
    }

    private void processSaleItems(List<SaleItemResponse> items) {
        for (SaleItemResponse itemDto : items) {
            processSingleItem(itemDto);
        }
    }

    private void processSingleItem(SaleItemResponse itemDto) {
        productService.decreaseStock(itemDto.productId(), itemDto.quantity());

        Product product = findById(itemDto.productId());
        BigDecimal totalPrice = calculateTotalPrice(product, itemDto.quantity());
        
        SalesRecord salesRecord = new SalesRecord(product, itemDto.quantity(), totalPrice);
        salesRecordRepository.save(salesRecord);
    }

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find product with id: " + productId));
    }

    private BigDecimal calculateTotalPrice(Product product, Integer quantity) {
        return product.getPrice().multiply(new BigDecimal(quantity));
    }

    public List<SalesRecordResponse> findRecentSales(int limit) {
        return salesRecordRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(com.camping.admin.domain.entity.SalesRecord::getCreatedAt).reversed())
                .limit(limit)
                .map(SalesRecordResponse::from)
                .toList();
    }

    public DailyRevenueReportResponse generateDailyRevenueReport(LocalDate date) {
        BigDecimal totalSalesRevenue = calculateDailySalesRevenue(date);
        BigDecimal totalReservationRevenue = calculateDailyReservationRevenue(date);
        BigDecimal totalRentalRevenue = calculateDailyRentalRevenue(date);
        BigDecimal grandTotal = calculateGrandTotal(totalSalesRevenue, totalReservationRevenue, totalRentalRevenue);

        return new DailyRevenueReportResponse(date, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    private BigDecimal calculateDailySalesRevenue(LocalDate date) {
        return salesRecordRepository.findAll().stream()
                .filter(record -> record.getCreatedAt().toLocalDate().equals(date))
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDailyReservationRevenue(LocalDate date) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null && r.getReservationDate().equals(date))
                .map(this::calculateReservationRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDailyRentalRevenue(LocalDate date) {
        return rentalRecordRepository.findAll().stream()
                .filter(rr -> rr.getCreatedAt().toLocalDate().equals(date))
                .map(rr -> rr.getProduct().getPrice().multiply(new BigDecimal(rr.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateReservationRevenue(com.camping.admin.domain.entity.Reservation r) {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
        if (nights < 1) nights = 1;
        return new BigDecimal(nights).multiply(new BigDecimal("50000"));
    }

    private BigDecimal calculateGrandTotal(BigDecimal... revenues) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal revenue : revenues) {
            total = total.add(revenue);
        }
        return total;
    }

    public RangeRevenueReportResponse generateRangeRevenueReport(LocalDate from, LocalDate to) {
        BigDecimal totalSalesRevenue = calculateRangeSalesRevenue(from, to);
        BigDecimal totalReservationRevenue = calculateRangeReservationRevenue(from, to);
        BigDecimal totalRentalRevenue = calculateRangeRentalRevenue(from, to);
        BigDecimal grandTotal = calculateGrandTotal(totalSalesRevenue, totalReservationRevenue, totalRentalRevenue);

        return new RangeRevenueReportResponse(from, to, totalReservationRevenue, totalSalesRevenue, totalRentalRevenue, grandTotal);
    }

    private BigDecimal calculateRangeSalesRevenue(LocalDate from, LocalDate to) {
        return salesRecordRepository.findAll().stream()
                .filter(record -> isDateInRange(record.getCreatedAt().toLocalDate(), from, to))
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateRangeReservationRevenue(LocalDate from, LocalDate to) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> isDateInRange(r.getReservationDate(), from, to))
                .map(this::calculateReservationRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateRangeRentalRevenue(LocalDate from, LocalDate to) {
        return rentalRecordRepository.findAll().stream()
                .filter(rr -> isDateInRange(rr.getCreatedAt().toLocalDate(), from, to))
                .map(rr -> rr.getProduct().getPrice().multiply(new BigDecimal(rr.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isDateInRange(LocalDate date, LocalDate from, LocalDate to) {
        return (date.isEqual(from) || date.isAfter(from)) && (date.isEqual(to) || date.isBefore(to));
    }

    public List<RevenueEntryResponse> generateRangeRevenueEntries(LocalDate from, LocalDate to) {
        List<RevenueEntryResponse> entries = new ArrayList<>();
        addSalesEntries(entries, from, to);
        addReservationEntries(entries, from, to);
        addRentalEntries(entries, from, to);
        entries.sort(Comparator.comparing(RevenueEntryResponse::occurredAt));
        return entries;
    }

    private void addSalesEntries(List<RevenueEntryResponse> entries, LocalDate from, LocalDate to) {
        salesRecordRepository.findAll().stream()
                .filter(record -> isDateInRange(record.getCreatedAt().toLocalDate(), from, to))
                .forEach(record -> entries.add(createSalesEntry(record)));
    }

    private void addReservationEntries(List<RevenueEntryResponse> entries, LocalDate from, LocalDate to) {
        reservationRepository.findAll().stream()
                .filter(r -> r.getReservationDate() != null)
                .filter(r -> isDateInRange(r.getReservationDate(), from, to))
                .forEach(r -> entries.add(createReservationEntry(r)));
    }

    private void addRentalEntries(List<RevenueEntryResponse> entries, LocalDate from, LocalDate to) {
        rentalRecordRepository.findAll().stream()
                .filter(rr -> isDateInRange(rr.getCreatedAt().toLocalDate(), from, to))
                .forEach(rr -> entries.add(createRentalEntry(rr)));
    }

    private RevenueEntryResponse createSalesEntry(SalesRecord record) {
        return new RevenueEntryResponse(
                RevenueEntryResponse.EntryType.SALE,
                record.getProduct().getName() + " 외",
                record.getTotalPrice(),
                record.getCreatedAt()
        );
    }

    private RevenueEntryResponse createReservationEntry(com.camping.admin.domain.entity.Reservation r) {
        BigDecimal revenue = calculateReservationRevenue(r);
        return new RevenueEntryResponse(
                RevenueEntryResponse.EntryType.RESERVATION,
                "예약 #" + r.getId(),
                revenue,
                r.getCreatedAt()
        );
    }

    private RevenueEntryResponse createRentalEntry(com.camping.admin.domain.entity.RentalRecord rr) {
        String description = rr.getProduct().getName() + 
                (rr.getReservation() != null ? " (예약#" + rr.getReservation().getId() + ")" : "");
        BigDecimal revenue = rr.getProduct().getPrice().multiply(new BigDecimal(rr.getQuantity()));
        return new RevenueEntryResponse(
                RevenueEntryResponse.EntryType.RENTAL,
                description,
                revenue,
                rr.getCreatedAt()
        );
    }
}
