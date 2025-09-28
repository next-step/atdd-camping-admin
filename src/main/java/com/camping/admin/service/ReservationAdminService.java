package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationAdminService {

    private final ReservationRepository reservationRepository;

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ResponseEntity<ReservationResponse> updateReservationStatus(Long reservationId, UpdateReservationStatusRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        if (!request.isValid()) {
            return new ResponseEntity<>(ReservationResponse.from(reservation), HttpStatus.BAD_REQUEST);
        }

        if (reservation.isCanceled() && request.getStatus().equals(ReservationStatus.CHECKED_IN.name())) {
            return new ResponseEntity<>(ReservationResponse.from(reservation), HttpStatus.BAD_REQUEST);
        }
        // 단순히 그대로 대입
        reservation.setStatus(request.getStatus());
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }
}
