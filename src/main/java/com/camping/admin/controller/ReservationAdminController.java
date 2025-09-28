package com.camping.admin.controller;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.repository.ReservationRepository;

import java.util.List;
import java.util.Map;

import com.camping.admin.service.ReservationAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationAdminService reservationAdminService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> result = reservationAdminService.getAllReservations();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestBody Map<String, Object> body) {
        return reservationAdminService.updateReservationStatus(reservationId, body);
    }
}
