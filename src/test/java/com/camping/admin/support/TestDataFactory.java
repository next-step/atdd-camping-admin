package com.camping.admin.support;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.ReservationRepository;
import org.springframework.stereotype.Component;

@Component
public class TestDataFactory {

    private final ReservationRepository reservationRepository;

    public TestDataFactory(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation getConfirmedReservation() {
        return reservationRepository.findAll().stream()
                .filter(r -> "CONFIRMED".equals(r.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CONFIRMED 상태의 예약이 없습니다."));
    }

    public Reservation getReservationWithStatus(String status) {
        return reservationRepository.findAll().stream()
                .filter(r -> status.equals(r.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(status + " 상태의 예약이 없습니다."));
    }
}