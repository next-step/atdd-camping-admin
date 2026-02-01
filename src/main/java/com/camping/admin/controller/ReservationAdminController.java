package com.camping.admin.controller;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.ReservationStatusUpdateRequest;
import com.camping.admin.service.ReservationService;
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
        var responses = reservationService.getAll();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestBody ReservationStatusUpdateRequest updateReq) {
        reservationService.updateStatus(reservationId, updateReq);
        var response = reservationService.get(reservationId);
        return ResponseEntity.ok(response);
    }
}
