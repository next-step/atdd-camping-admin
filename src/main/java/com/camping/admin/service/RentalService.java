package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.RentalResponse;
import com.camping.admin.exception.BusinessException;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("상품을 찾을 수 없습니다: " + productId, HttpStatus.NOT_FOUND));

        if (product.getProductType() != ProductType.RENTAL) {
            throw new BusinessException("대여 불가 상품입니다.", HttpStatus.BAD_REQUEST);
        }

        if (quantity <= 0) {
            throw new BusinessException("수량은 1개 이상이여햐 합니다.", HttpStatus.BAD_REQUEST);
        }

        productService.decreaseStock(productId, quantity);

        Reservation reservation = null;
        if (reservationId != null) {
            reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new BusinessException("예약을 찾을 수 없습니다: " + reservationId, HttpStatus.NOT_FOUND));
        }

        RentalRecord rentalRecord = new RentalRecord(reservation, product, quantity);
        RentalRecord savedRentalRecord = rentalRecordRepository.save(rentalRecord);

        return RentalResponse.from(savedRentalRecord);
    }

    @Transactional
    public RentalResponse markAsReturned(Long rentalRecordId) {
        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
                .orElseThrow(() -> new BusinessException("대여 기록을 찾을 수 없습니다: " + rentalRecordId, HttpStatus.NOT_FOUND));

        if (rentalRecord.getIsReturned()) {
            throw new BusinessException("이미 반납된 상품입니다.", HttpStatus.CONFLICT);
        }
        rentalRecord.setReturned(true);
        
        productService.increaseStock(rentalRecord.getProduct().getId(), rentalRecord.getQuantity());
        
        return RentalResponse.from(rentalRecord);
    }
}