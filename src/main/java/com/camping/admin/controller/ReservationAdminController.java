package com.camping.admin.controller;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.repository.ReservationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationRepository reservationRepository;

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
            @RequestBody UpdateReservationStatusRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        // DTO에서 이미 유효성 검사가 완료된 enum 값을 사용
        reservation.setStatus(request.getStatus());

        reservationRepository.save(reservation);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }
}