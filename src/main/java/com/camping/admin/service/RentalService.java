package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.service.dto.RentalRequest;
import com.camping.admin.service.dto.RentalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRecordRepository rentalRecordRepository;
    private final ReservationService reservationService;
    private final ProductService productService;

    public List<RentalResponse> findAll() {
        return rentalRecordRepository.findAll().stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalResponse createRental(RentalRequest request) {
        final Reservation reservation = reservationService.findActiveReservation(request.reservationId());
        final Product product = productService.decreaseStock(request.productId(), request.quantity());
        final RentalRecord rental = rentalRecordRepository.save(new RentalRecord(reservation, product, request.quantity()));

        return RentalResponse.from(rental);
    }

    @Transactional
    public RentalResponse markAsReturned(Long rentalRecordId) {
        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find rental record with id: " + rentalRecordId));
        
        if (rentalRecord.getIsReturned()) {
            throw new IllegalStateException("This item has already been returned.");
        }
        rentalRecord.setReturned(true);
        
        productService.increaseStock(rentalRecord.getProduct().getId(), rentalRecord.getQuantity());
        
        return RentalResponse.from(rentalRecord);
    }
}