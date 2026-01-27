package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.entity.ReservationStatusHistory;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.exception.ReservationNotFoundException;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.repository.ReservationStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationStatusHistoryRepository reservationStatusHistoryRepository;

    @Transactional
    public Reservation updateStatus(Long reservationId, String status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        ReservationStatus oldStatus = ReservationStatus.valueOf(reservation.getStatus());
        ReservationStatus newStatus = ReservationStatus.valueOf(status);

        reservation.updateStatus(status);

        reservationStatusHistoryRepository.save(
                new ReservationStatusHistory(reservation, oldStatus, newStatus));

        return reservation;
    }
}
