package com.camping.admin.controller;

import com.camping.admin.dto.RentalCreateRequest;
import com.camping.admin.dto.RentalRecordResponse;
import com.camping.admin.service.RentalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/rentals")
@RequiredArgsConstructor
public class RentalAdminController {

    private final RentalService rentalService;

    @GetMapping
    public ResponseEntity<List<RentalRecordResponse>> getAllRentals() {
        List<RentalRecordResponse> rentals = rentalService.getAll();
        return ResponseEntity.ok(rentals);
    }

    @PostMapping
    public ResponseEntity<RentalRecordResponse> createRental(@RequestBody RentalCreateRequest createReq) {
        Long rentalRecordId = rentalService.create(createReq);
        var response = rentalService.get(rentalRecordId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{rentalRecordId}/return")
    public ResponseEntity<RentalRecordResponse> markAsReturned(@PathVariable Long rentalRecordId) {
        rentalService.returnItem(rentalRecordId);
        var response = rentalService.get(rentalRecordId);
        return ResponseEntity.ok(response);
    }
}