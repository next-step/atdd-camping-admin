package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.exception.ReservationNotFoundException;
import com.camping.admin.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    public ReservationResponse updateReservationStatus(
            Long reservationId,
            UpdateReservationStatusRequest request
    ) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Cannot find reservation with id: " + reservationId));

        reservation.changeStatus(request.getStatus());
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }

    public ReservationResponse checkIn(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Cannot find reservation with id: " + reservationId));

        reservation.checkIn();
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }

    public ReservationResponse checkOut(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Cannot find reservation with id: " + reservationId));

        reservation.checkOut();
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }
}
