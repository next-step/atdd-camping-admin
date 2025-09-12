package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationResponse updateReservation(Long id, String statusValue) {
        Reservation reservation = findReservationById(id);
        ReservationStatus status = ReservationStatus.getByString(statusValue);
        checkValidUpdate(reservation, status);
        reservation.setStatus(status);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private Reservation findReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + id));
    }

    private void checkValidUpdate(Reservation reservation, ReservationStatus status) {
        if (reservation.isCancelled() && status.isCancelled()) {
            throw new IllegalStateException("이미 취소된 예약 입니다.");
        }
    }
}
