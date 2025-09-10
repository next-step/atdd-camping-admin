package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.exception.EntityNotFoundException;
import com.camping.admin.exception.ValidationException;
import com.camping.admin.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse updateReservationStatus(Long reservationId, Map<String, Object> body) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find reservation with id: " + reservationId));

        if (body == null || body.isEmpty()) {
            throw new ValidationException("Request body cannot be empty");
        }

        updateStatus(reservation, body);
        reservationRepository.save(reservation);
        return ReservationResponse.from(reservation);
    }

    private void updateStatus(Reservation reservation, Map<String, Object> body) {
        Object statusObj = body.get("status");
        if (statusObj != null) {
            String statusValue = statusObj.toString();
            if (!statusValue.isBlank()) {
                reservation.setStatus(statusValue);
            }
        }
    }
}
