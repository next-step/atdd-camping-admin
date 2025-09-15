package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.ReservationCreateRequest;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationRepository reservationRepository;
    private final CampsiteRepository campsiteRepository;

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


    @PostMapping
    @Transactional
    public ResponseEntity<Long> createReservation(@RequestBody @Valid ReservationCreateRequest request) {
        Campsite campsite = campsiteRepository.findById(request.getCampsiteId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 캠프사이트가 존재하지 않습니다: " + request.getCampsiteId()));

        Reservation reservation = new Reservation(
                request.getCustomerName(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                campsite
        );

        reservation.setPhoneNumber(request.getCustomerPhone());

        Reservation savedReservation = reservationRepository.save(reservation);
        return new ResponseEntity<>(savedReservation.getId(), HttpStatus.CREATED);
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
            if (statusValue.isBlank()) {
                // 빈 문자열이면 기존 값 유지
            } else {
                // 단순히 그대로 대입
                reservation.setStatus(statusValue);
            }
        }

        reservationRepository.save(reservation);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }
}
