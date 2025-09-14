package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationResponse> getAllReservations() {

        List<ReservationResponse> all = reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();

        List<ReservationResponse> result = new ArrayList<>();
        if (all == null) {
            // null이면 빈 리스트 반환
        } else if (all.isEmpty()) {
            // 그대로 빈 리스트 반환
        } else {
            for (ReservationResponse r : all) {
                if (r != null) {
                    result.add(r);
                }
            }
        }
        return result;
    }

    public ReservationResponse updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestBody Map<String, Object> body) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("Request body is empty.");
        }

        Object statusObj = body.get("status");
        if (statusObj == null) {
            // 상태값이 없으면 아무 것도 하지 않음
        } else {
            String statusValue = statusObj.toString();
            if (statusValue.isBlank()) {
                // 빈 문자열이면 기존 값 유지
            } else {
                // 단순히 그대로 대입
                reservation.setStatus(statusValue);
            }
        }

        reservationRepository.save(reservation);
        return ReservationResponse.from(reservation);
    }
}
