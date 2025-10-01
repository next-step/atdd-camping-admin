package com.camping.admin.controller;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> reservations = reservationService.findAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long reservationId,
            @Valid @RequestBody UpdateReservationStatusRequest request
    ) {
        ReservationResponse updatedReservation = reservationService.updateReservationStatus(
                reservationId,
                request.getStatus()
        );
        return ResponseEntity.ok(updatedReservation);
    }
}