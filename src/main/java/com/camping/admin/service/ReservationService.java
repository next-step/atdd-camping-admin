package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.ReservationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional
    public Reservation updateStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));
        reservation.updateStatus(status);
        return reservation;
    }

    public Optional<Reservation> findByConfirmationCode(String code) {
        return reservationRepository.findByConfirmationCode(code);
    }
}