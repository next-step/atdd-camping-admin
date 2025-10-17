package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.ReservationRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation updateReservationStatus(Long reservationId, String statusValue) {
        if (StringUtils.isBlank(statusValue)) {
            throw new IllegalArgumentException("올바른 상태를 입력해 주세요.");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find reservation with id: " + reservationId));

        ReservationStatus status = ReservationStatus.fromCode(statusValue);
        reservation.setStatus(status.getCode());
        return reservationRepository.save(reservation);
    }
}
