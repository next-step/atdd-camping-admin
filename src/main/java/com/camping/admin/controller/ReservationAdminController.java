package com.camping.admin.controller;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
public class ReservationAdminController {
    private final ReservationService reservationService;

    public ReservationAdminController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> response = reservationService.getAllReservations();
        return new ResponseEntity<>(response, HttpStatus.OK) ;
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestBody UpdateReservationStatusRequest request
    ) {
        ReservationResponse response = reservationService.updateReservationStatus(reservationId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}