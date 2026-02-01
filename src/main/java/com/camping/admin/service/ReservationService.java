package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.ReservationStatusUpdateRequest;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    // ===== Command =====

    @Transactional
    public void updateStatus(Long reservationId, ReservationStatusUpdateRequest updateReq) {
        var reservation = findById(reservationId);
        reservation.updateStatus(updateReq.status());
    }

    // ===== Query =====

    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ReservationResponse get(Long reservationId) {
        return ReservationResponse.from(findById(reservationId));
    }

    // ===== Helper =====

    private Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find reservation with id: " + reservationId));
    }
}
