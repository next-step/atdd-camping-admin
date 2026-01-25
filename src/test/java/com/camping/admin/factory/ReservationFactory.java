package com.camping.admin.factory;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.api.TestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReservationFactory{


    @Autowired
    public CampsiteRepository campsiteRepository;

    @Autowired
    public ReservationRepository reservationRepository;

    @Autowired
    public TestContext testContext;


    public void createReservation(String customerName, String siteNumber, String status) {
        Campsite campsite = campsiteRepository.findBySiteNumber(siteNumber)
                .orElseGet(() -> campsiteRepository.save(
                        new Campsite(siteNumber, "테스트 캠프사이트", 4)));

        Reservation reservation = new Reservation(
                customerName,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                campsite
        );
        reservation.setStatus(status);
        reservation.setReservationDate(LocalDate.now());

        Reservation saved = reservationRepository.save(reservation);
        testContext.getReservation().setId(saved.getId());
    }
}
