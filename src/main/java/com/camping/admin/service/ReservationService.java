package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.ReservationStatusUpdateRequest;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation getReservationById(Long reservationId) {
        return findReservationById(reservationId);
    }

    public Reservation updateReservationStatus(final Long reservationId, final ReservationStatusUpdateRequest request) {
        Reservation reservation = findReservationById(reservationId);
        reservation.updateStatus(request);
        return reservationRepository.save(reservation);
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));
    }
}
