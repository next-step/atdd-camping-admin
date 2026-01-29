package com.camping.admin.controller;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.ReservationStatusUpdateRequest;
import com.camping.admin.service.ReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestBody ReservationStatusUpdateRequest request) {
        return ResponseEntity.ok(reservationService.updateStatus(reservationId, request));
    }
}
