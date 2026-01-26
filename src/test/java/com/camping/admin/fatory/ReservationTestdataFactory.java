package com.camping.admin.fatory;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional
@Component
@RequiredArgsConstructor
public class ReservationTestdataFactory {

    private final ReservationRepository reservationRepository;

    public Reservation createReservation(String customerName, Campsite campsite) {
        Reservation reservation = new Reservation(
            customerName,
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(3),
            campsite
        );
        return reservationRepository.save(reservation);
    }

    public Reservation createReservationWithStatus(String customerName, Campsite campsite, String status) {
        Reservation reservation = createReservation(customerName, campsite);
        reservation.setStatus(ReservationStatus.valueOf(status).name());
        return reservationRepository.save(reservation);
    }
}
