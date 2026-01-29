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

import static com.camping.admin.domain.enums.ReservationStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * 전체 예약 목록 조회
     * - 조회 로직을 서비스 계층에서 관리
     */
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 예약 상태 변경
     * - 비즈니스 규칙 검증은 서비스 계층의 책임
     * - 트랜잭션 범위 내에서 상태 변경 및 저장
     */
    @Transactional
    public ReservationResponse updateStatus(Long reservationId, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException("예약을 찾을 수 없습니다: " + reservationId, HttpStatus.NOT_FOUND));

        ReservationStatus currentStatus = reservation.getStatus();
        validateStatusChange(currentStatus, newStatus);
        reservation.setStatus(newStatus);
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }

    /**
     * 상태 변경 가능 여부 검증
     * - 도메인 규칙: 취소/체크인/체크아웃/거절된 예약은 취소 불가
     */
    private void validateStatusChange(ReservationStatus currentStatus, ReservationStatus newStatus) {
        if (currentStatus == CANCELLED) {
            throw new BusinessException("이미 취소된 예약은 복구할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (currentStatus == CHECKED_IN && newStatus == CANCELLED) {
            throw new BusinessException("체크인된 예약은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (currentStatus == CHECKED_OUT && newStatus == CANCELLED) {
            throw new BusinessException("이미 완료된 예약은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (currentStatus == REJECTED && newStatus == CANCELLED) {
            throw new BusinessException("이미 거절된 예약은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}