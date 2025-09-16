package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation create(
        Campsite campsite,
        String customerName,
        LocalDate startDate,
        LocalDate endDate,
        String phoneNumber,
        LocalDate reservationDate
    ) {
        String confirmationCode = ReservationCodeGenerator.generate();
        Reservation reservation = Reservation.create(
            customerName,
            startDate,
            endDate,
            campsite,
            phoneNumber,
            reservationDate,
            confirmationCode
        );

        return reservationRepository.save(reservation);
    }

    public Reservation update(Long reservationId, ReservationStatus reservationStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));
        reservation.updateStatus(reservationStatus);
        return reservationRepository.save(reservation);
    }

    public Reservation updateConfirmCode(Long reservationId, String confirmCode) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));
        reservation.setConfirmationCode(confirmCode);
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
