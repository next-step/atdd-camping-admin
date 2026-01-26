package com.camping.admin.controller;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.exception.BusinessException;
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
            @RequestBody Map<String, Object> body) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        if (body == null || body.isEmpty()) {
            return new ResponseEntity<>(ReservationResponse.from(reservation), HttpStatus.BAD_REQUEST);
        }

        if (reservation.isCanceled()) {
            throw new BusinessException("이미 취소된 예약입니다.", HttpStatus.BAD_REQUEST);
        }

        Object statusObj = body.get("status");
        if (statusObj == null) {
            throw new BusinessException("상태값은 필수입니다.", HttpStatus.BAD_REQUEST);
        }

        String statusValue = statusObj.toString();
        if (statusValue.isBlank()) {
            throw new BusinessException("상태값은 필수입니다.", HttpStatus.BAD_REQUEST);
        }

        ReservationStatus newStatus = ReservationStatus.valueOf(statusValue);
        reservation.setStatus(newStatus);

        reservationRepository.save(reservation);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }
}