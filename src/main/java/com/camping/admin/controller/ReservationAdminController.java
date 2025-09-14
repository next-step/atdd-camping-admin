package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.CreateReservationRequest;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationRepository reservationRepository;
    private final CampsiteRepository campsiteRepository;
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody CreateReservationRequest request) {
        request.validate();
        Campsite campsite = campsiteRepository.findById(request.getCampsiteId())
            .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + request.getCampsiteId()));
        Reservation createdReservation = reservationService.create(
            campsite,
            request.getCustomerName(),
            request.getStartDate(),
            request.getEndDate(),
            request.getPhoneNumber(),
            request.getReservationDate()
        );

        return new ResponseEntity<>(ReservationResponse.from(createdReservation), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAll();
        List<ReservationResponse> result = reservations.stream().map(ReservationResponse::from).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.get(reservationId);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
        @PathVariable Long reservationId,
        @RequestBody UpdateReservationStatusRequest request) {
        request.validate();

        Reservation reservation = reservationService.update(reservationId, request.getStatus());
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }
}