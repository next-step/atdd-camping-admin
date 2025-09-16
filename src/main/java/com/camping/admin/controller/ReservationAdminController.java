package com.camping.admin.controller;

import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.ReservationUpdateRequest;
import java.util.List;

import com.camping.admin.service.ReservationService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations(@RequestParam @Nullable ReservationStatus status) {
        List<ReservationResponse> response = reservationService.readAll(status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestBody ReservationUpdateRequest request
    ) {
        var response = reservationService.updateReservation(reservationId, request.status());
        return ResponseEntity.ok(response);
    }
}
