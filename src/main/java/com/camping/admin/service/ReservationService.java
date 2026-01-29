package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.exception.BusinessException;
import com.camping.admin.repository.ReservationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                .collect(Collectors.toList());
    }

    /**
     * 예약 상태 변경
     * - 도메인 규칙 검증은 Reservation 엔티티에 위임 (Tell, Don't Ask)
     */
    @Transactional
    public ReservationResponse updateStatus(Long reservationId, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException("예약을 찾을 수 없습니다: " + reservationId, HttpStatus.NOT_FOUND));

        reservation.changeStatus(newStatus);

        return ReservationResponse.from(reservation);
    }
}