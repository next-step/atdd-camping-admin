package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * 예약 상태를 업데이트 한다.
     *
     * @param reservationId 예약 ID
     * @param request       요청 (예약 상태)
     * @return 업데이트된 예약
     */
    public Reservation updateReservationStatus(Long reservationId,
                                               UpdateReservationStatusRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        reservation.updateStatus(request.status());

        return reservationRepository.save(reservation);
    }
}
