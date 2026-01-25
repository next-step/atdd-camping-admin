package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public @Nullable Reservation findActiveReservation(final Long reservationId) {
        if (reservationId == null) {
            return null;
        }

        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        if (reservation.isCancelled()) {
            throw new IllegalStateException("Cannot create rental for a cancelled reservation.");
        }

        return reservation;
    }
}
