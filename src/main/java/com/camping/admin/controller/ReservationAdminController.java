package com.camping.admin.controller;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.service.ReservationService;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody Map<String, Object> body) {

        if (body == null || body.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Object statusObj = body.get("status");
        if (statusObj == null) {
            return ResponseEntity.badRequest().build();
        }

        String statusValue = statusObj.toString();
        ReservationResponse updatedReservation = reservationService.updateReservationStatus(reservationId, statusValue);

        return ResponseEntity.ok(updatedReservation);
    }
}