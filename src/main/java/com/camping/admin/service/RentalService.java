package com.camping.admin.service;

import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.RentalCreateRequest;
import com.camping.admin.dto.RentalRecordResponse;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRecordRepository rentalRecordRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    // ===== Command =====

    @Transactional
    public Long create(RentalCreateRequest createReq) {
        var product = productRepository.findById(createReq.productId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + createReq.productId()));

        if (!product.isRental()) {
            throw new IllegalArgumentException("Product is not a rental item.");
        }

        product.decreaseStock(createReq.quantity());

        Reservation reservation = hasReservation(createReq.reservationId())
                ? findReservation(createReq.reservationId())
                : null;

        var newRentalRecord = RentalRecord.create(reservation, product, createReq.quantity(), LocalDateTime.now());
        rentalRecordRepository.save(newRentalRecord);

        return newRentalRecord.getId();
    }

    @Transactional
    public void returnItem(Long rentalRecordId) {
        var rentalRecord = findById(rentalRecordId);
        rentalRecord.returnItem();
    }

    // ===== Query =====

    public List<RentalRecordResponse> getAll() {
        return rentalRecordRepository.findAll().stream()
                .map(RentalRecordResponse::from)
                .toList();
    }

    public RentalRecordResponse get(Long rentalRecordId) {
        return RentalRecordResponse.from(findById(rentalRecordId));
    }

    // ===== Helper =====

    private boolean hasReservation(Long reservationId) {
        return reservationId != null;
    }

    private Reservation findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));
    }

    private RentalRecord findById(Long rentalRecordId) {
        return rentalRecordRepository.findById(rentalRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find rental record with id: " + rentalRecordId));
    }
}