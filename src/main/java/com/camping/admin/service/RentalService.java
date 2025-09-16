package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.RentalResponse;
import com.camping.admin.exception.*;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<RentalResponse> findAll() {
        return rentalRecordRepository.findAll().stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalResponse createRental(Long productId, Integer quantity, Long reservationId) {
        RentalRecord rentalRecord = new RentalRecord(
                findReservationById(reservationId),
                findProductById(productId),
                quantity
        );
        RentalRecord savedRentalRecord = rentalRecordRepository.save(rentalRecord);
        return RentalResponse.from(savedRentalRecord);
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Cannot find product with id: " + productId));
    }

    private Reservation findReservationById(Long reservationId) {
        return Optional.ofNullable(reservationId)
                .map(id -> reservationRepository.findById(id)
                        .orElseThrow(() -> new ReservationNotFoundException("Cannot find reservation with id: " + id)))
                .orElse(null);  // Walk-in rental
    }

    @Transactional
    public RentalResponse markAsReturned(Long rentalRecordId) {
        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
                .orElseThrow(() -> new RentalNotFoundException("Cannot find rental record with id: " + rentalRecordId));
        rentalRecord.returnProduct();
        return RentalResponse.from(rentalRecord);
    }
}