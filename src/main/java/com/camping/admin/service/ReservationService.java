package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation update(Long reservationId, ReservationStatus reservationStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));
        reservation.updateStatus(reservationStatus);
        return reservationRepository.save(reservation);
    }

    public Reservation get(Long reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll(); // [Note] 여기서만 간편히 .findAll()을 사용해서 구현
    }
}
