package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationStatusUpdateRequest;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Reservation updateStatus(Long reservationId, ReservationStatusUpdateRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        if (!StringUtils.hasText(request.status())) {
            return reservation;
        }

        ReservationStatus newStatus = ReservationStatus.valueOf(request.status());

        if (ReservationStatus.CONFIRMED.equals(newStatus)) {
            validateReservationNotConflict(reservation);
        }

        reservation.setStatus(newStatus);
        return reservationRepository.save(reservation);
    }

    private void validateReservationNotConflict(Reservation reservation) {
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                reservation.getCampsite().getId(),
                reservation.getEndDate(),
                reservation.getStartDate()
        );

        if (!overlappingReservations.isEmpty()) {
            throw new IllegalArgumentException("동일 기간에 이미 확정된 예약이 존재합니다.");
        }
    }
}
