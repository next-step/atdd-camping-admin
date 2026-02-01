package com.camping.admin.helpers;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DatabaseHelper {

    private final ReservationRepository reservationRepository;
    private final RentalRecordRepository rentalRecordRepository;
    private final CampsiteRepository campsiteRepository;
    private final ProductRepository productRepository;

    public DatabaseHelper(
            ReservationRepository reservationRepository,
            RentalRecordRepository rentalRecordRepository,
            CampsiteRepository campsiteRepository,
            ProductRepository productRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.rentalRecordRepository = rentalRecordRepository;
        this.campsiteRepository = campsiteRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Long setupReservation(String status) {
        var campsite = getOrCreateTestCampsite();

        var reservation = new Reservation(
            "테스트고객",
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            campsite
        );
        reservation.setStatus(status);

        return reservationRepository.save(reservation).getId();
    }

    @Transactional
    public Long setupReservationWithRental(String status) {
        var campsite = getOrCreateTestCampsite();
        var product = getOrCreateTestProduct();

        var reservation = new Reservation(
            "테스트고객",
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            campsite
        );
        reservation.setStatus(status);
        reservationRepository.save(reservation);

        var rental = new RentalRecord(reservation, product, 1);
        rentalRecordRepository.save(rental);

        return reservation.getId();
    }

    private Campsite getOrCreateTestCampsite() {
        return campsiteRepository.findBySiteNumber("TEST-001")
            .orElseGet(() -> {
                var campsite = new Campsite("TEST-001", "테스트 캠프사이트", 4);
                return campsiteRepository.save(campsite);
            });
    }

    private Product getOrCreateTestProduct() {
        return productRepository.findAll().stream()
            .findFirst()
            .orElseGet(() -> {
                var product = new Product("텐트", 10, BigDecimal.valueOf(10000), ProductType.RENTAL);
                return productRepository.save(product);
            });
    }
}
