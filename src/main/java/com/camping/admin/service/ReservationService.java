package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationStatusUpdateRequest;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

        reservation.setStatus(ReservationStatus.valueOf(request.status()));
        return reservationRepository.save(reservation);
    }
}
