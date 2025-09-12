package com.camping.admin.controller;

import com.camping.admin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/test")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "test")
public class TestController {

    private final CampsiteRepository campsiteRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final RentalRecordRepository rentalRecordRepository;
    private final SalesRecordRepository salesRecordRepository;
    private final CustomerRepository customerRepository;

    @DeleteMapping("/reset")
    public ResponseEntity<String> resetDatabase() {
        rentalRecordRepository.deleteAll();
        salesRecordRepository.deleteAll();
        reservationRepository.deleteAll();
        campsiteRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        return ResponseEntity.ok("Database reset completed");
    }
}