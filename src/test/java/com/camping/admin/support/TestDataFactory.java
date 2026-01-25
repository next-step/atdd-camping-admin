package com.camping.admin.support;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import org.springframework.stereotype.Component;

@Component
public class TestDataFactory {

    private final ReservationRepository reservationRepository;
    private final CampsiteRepository campsiteRepository;

    public TestDataFactory(ReservationRepository reservationRepository, CampsiteRepository campsiteRepository) {
        this.reservationRepository = reservationRepository;
        this.campsiteRepository = campsiteRepository;
    }

    public Reservation getReservationWithStatus(ReservationStatus status) {
        return reservationRepository.findAll().stream()
                .filter(r -> status.equals(r.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(status + " 상태의 예약이 없습니다."));
    }

    public Campsite getCampsiteBySiteNumber(String siteNumber) {
        return campsiteRepository.findBySiteNumber(siteNumber)
                .orElseThrow(() -> new RuntimeException("사이트 번호 " + siteNumber + "에 해당하는 캠프사이트가 없습니다."));
    }
}