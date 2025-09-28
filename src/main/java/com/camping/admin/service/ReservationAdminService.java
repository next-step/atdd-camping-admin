package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationAdminService {

    private final ReservationRepository reservationRepository;

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ResponseEntity<ReservationResponse> updateReservationStatus(Long reservationId, Map<String, Object> requestBody) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        if (requestBody == null || requestBody.isEmpty()) {
            return new ResponseEntity<>(ReservationResponse.from(reservation), HttpStatus.BAD_REQUEST);
        }

        Object statusObj = requestBody.get("status");
        if (statusObj == null) {
            // 상태값이 없으면 아무 것도 하지 않음
        } else {
            String statusValue = statusObj.toString();
            if (statusValue.isBlank()) {
                // 빈 문자열이면 기존 값 유지
            } else {
                if (reservation.isCanceled() && statusValue.equals(ReservationStatus.CHECKED_IN.name())) {
                    return new ResponseEntity<>(ReservationResponse.from(reservation), HttpStatus.BAD_REQUEST);
                }
                // 단순히 그대로 대입
                reservation.setStatus(statusValue);
            }
        }

        reservationRepository.save(reservation);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }
}
