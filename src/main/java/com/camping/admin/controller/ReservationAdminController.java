package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.CreateReservationRequest;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.service.ReservationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationRepository reservationRepository;
    private final CampsiteRepository campsiteRepository;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody CreateReservationRequest request) {
        request.validate();
        Campsite campsite = campsiteRepository.findById(request.getCampsiteId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + request.getCampsiteId()));
        String confirmationCode = ReservationCodeGenerator.generate();

        Reservation reservation = Reservation.create(
            request.getCustomerName(),
            request.getStartDate(),
            request.getEndDate(),
            campsite,
            request.getPhoneNumber(),
            request.getReservationDate(),
            confirmationCode
        );
        Reservation saved = reservationRepository.save(reservation);
        return new ResponseEntity<>(ReservationResponse.from(saved), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> all = reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());

        List<ReservationResponse> result = new ArrayList<>();
        if (all == null) {
            // null이면 빈 리스트 반환
        } else if (all.isEmpty()) {
            // 그대로 빈 리스트 반환
        } else {
            for (ReservationResponse r : all) {
                if (r != null) {
                    result.add(r);
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestBody Map<String, Object> body) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        if (body == null || body.isEmpty()) {
            return new ResponseEntity<>(ReservationResponse.from(reservation), HttpStatus.BAD_REQUEST);
        }

        Object statusObj = body.get("status");
        if (statusObj == null) {
            // 상태값이 없으면 아무 것도 하지 않음
        } else {
            String statusValue = statusObj.toString();
            ReservationStatus reservationStatus = ReservationStatus.from(statusValue);
            if(reservationStatus == ReservationStatus.NONE) {
                throw new IllegalArgumentException("Invalid reservation status: " + statusValue);
            }

            if (statusValue.isBlank()) {
                // 빈 문자열이면 기존 값 유지
            } else {
                reservation.setStatus(reservationStatus);
            }
        }

        reservationRepository.save(reservation);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }
}