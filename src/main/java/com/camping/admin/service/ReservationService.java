package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.exception.NotFoundException;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Cannot find reservation with id: " + reservationId));
    }

    @Transactional
    public ReservationResponse updateStatus(Long reservationId, UpdateReservationStatusRequest request) {
        Reservation reservation = findById(reservationId);

        validateStatusUpdate(request);

        reservation.setStatus(request.getStatus());
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }

    private void validateStatusUpdate(UpdateReservationStatusRequest request) {
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        if (!isValidStatus(request.getStatus())) {
            throw new IllegalArgumentException("Invalid status: " + request.getStatus());
        }
    }

    private boolean isValidStatus(String status) {
        for (ReservationStatus rs : ReservationStatus.values()) {
            if (rs.name().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
