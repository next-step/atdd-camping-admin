package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.RentalResponse;
import com.camping.admin.exception.EntityNotFoundException;
import com.camping.admin.exception.RentalConflictException;
import com.camping.admin.exception.ValidationException;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
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
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final ProductService productService;

    public List<RentalResponse> findAll() {
        return rentalRecordRepository.findAll().stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalResponse createRental(Long productId, Integer quantity, Long reservationId) {
        validateRentalRequest(productId, quantity);
        
        Product product = findProductById(productId);
        validateRentalProduct(product);
        
        productService.decreaseStock(productId, quantity);
        
        Reservation reservation = findReservationById(reservationId);
        RentalRecord rentalRecord = createRentalRecord(reservation, product, quantity);
        
        return RentalResponse.from(rentalRecord);
    }

    private void validateRentalRequest(Long productId, Integer quantity) {
        if (productId == null) {
            throw new ValidationException("Product ID cannot be null");
        }
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("Quantity must be greater than 0");
        }
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find product with id: " + productId));
    }

    private void validateRentalProduct(Product product) {
        if (!product.isRentalType()) {
            throw new ValidationException("Product is not a rental item.");
        }
    }

    private Reservation findReservationById(Long reservationId) {
        if (reservationId == null) {
            return null;
        }
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find reservation with id: " + reservationId));
    }

    private RentalRecord createRentalRecord(Reservation reservation, Product product, Integer quantity) {
        RentalRecord rentalRecord = new RentalRecord(reservation, product, quantity);
        return rentalRecordRepository.save(rentalRecord);
    }

    @Transactional
    public RentalResponse markAsReturned(Long rentalRecordId) {
        RentalRecord rentalRecord = findRentalRecordById(rentalRecordId);
        processReturn(rentalRecord);
        return RentalResponse.from(rentalRecord);
    }

    private RentalRecord findRentalRecordById(Long rentalRecordId) {
        return rentalRecordRepository.findById(rentalRecordId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find rental record with id: " + rentalRecordId));
    }

    private void processReturn(RentalRecord rentalRecord) {
        rentalRecord.markAsReturned();
        productService.increaseStock(rentalRecord.getProduct().getId(), rentalRecord.getQuantity());
    }
}
