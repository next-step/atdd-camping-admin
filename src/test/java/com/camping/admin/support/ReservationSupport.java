package com.camping.admin.support;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.steps.TestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class ReservationSupport {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TestContext testContext;

    @Transactional
    public void 캠핑장에_예약이_되어있다(String customerName) {
        캠핑장에_예약이_되어있다(customerName, "PENDING");
    }

    @Transactional
    public void 캠핑장에_예약이_되어있다(String customerName, String status) {
        Campsite campsite = testContext.getCampsite();
        if (campsite == null) {
            throw new IllegalStateException("예약을 생성하기 위한 캠핑장 정보가 없습니다.");
        }

        Reservation reservation = new Reservation(
                customerName,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                campsite);
        reservation.setStatus(ReservationStatus.valueOf(status).name());

        Reservation savedReservation = reservationRepository.save(reservation);
        testContext.setReservationId(savedReservation.getId());
    }
}
