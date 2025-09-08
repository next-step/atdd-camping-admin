package com.camping.admin.controller;

import com.camping.admin.dto.CreateRentalRequest;
import com.camping.admin.dto.RentalResponse;
import com.camping.admin.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/rentals")
@RequiredArgsConstructor
public class RentalAdminController {

    private final RentalService rentalService;

    @GetMapping
    public ResponseEntity<List<RentalResponse>> getAllRentals() {
        List<RentalResponse> rentals = rentalService.findAll();
        return ResponseEntity.ok(rentals);
    }

    @PostMapping
    public ResponseEntity<RentalResponse> createRental(@RequestBody CreateRentalRequest request) {
        try {
            RentalResponse createdRental = rentalService.createRental(
                    request.getProductId(),
                    request.getQuantity(),
                    request.getReservationId()
            );
            return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Cannot find product")) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else if (e.getMessage().contains("not a rental item")) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PatchMapping("/{rentalRecordId}/return")
    public ResponseEntity<RentalResponse> markAsReturned(@PathVariable Long rentalRecordId) {
        RentalResponse updatedRental = rentalService.markAsReturned(rentalRecordId);
        return ResponseEntity.ok(updatedRental);
    }
}
