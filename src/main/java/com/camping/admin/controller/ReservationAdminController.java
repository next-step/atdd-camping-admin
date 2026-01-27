package com.camping.admin.controller;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> response = reservationService.findAll().stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable("reservationId") Long reservationId,
            @RequestBody @Valid UpdateReservationStatusRequest request) {
        Reservation updated = reservationService.updateStatus(reservationId, request.getStatus());
        return ResponseEntity.ok(ReservationResponse.from(updated));
    }
}
