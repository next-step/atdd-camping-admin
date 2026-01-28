package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.ReservationStatusUpdateRequest;
import com.camping.admin.repository.ReservationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse updateStatus(Long reservationId, ReservationStatusUpdateRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + reservationId));

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new IllegalArgumentException("빈 값은 허용되지 않습니다.");
        }

        reservation.setStatus(request.getStatus());
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }
}
