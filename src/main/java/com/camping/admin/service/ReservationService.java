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

        // 비즈니스 규칙 검증
        validateCheckIn(reservation);

        // 체크인 처리
        reservation.changeStatus(ReservationStatus.CHECKED_IN);
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }

    public ReservationResponse checkOut(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Cannot find reservation with id: " + reservationId));

        // 비즈니스 규칙 검증
        validateCheckOut(reservation);

        // 체크아웃 처리
        reservation.changeStatus(ReservationStatus.CHECKED_OUT);
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }

    private void validateCheckIn(Reservation reservation) {
        // 1. 예약 상태가 CONFIRMED가 아니면 체크인 불가
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
                throw new IllegalStateException("이미 체크인된 예약입니다");
            }
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new IllegalStateException("취소된 예약은 체크인할 수 없습니다");
            }
            throw new IllegalStateException("체크인할 수 없는 예약 상태입니다");
        }

        // 2. 체크인 날짜 검증
        LocalDate today = LocalDate.now();
        LocalDate startDate = reservation.getStartDate();

        // 체크인 날짜가 아직 안 된 경우
        if (today.isBefore(startDate)) {
            throw new IllegalArgumentException("체크인 날짜가 아직 되지 않았습니다");
        }

        // 체크인 날짜를 많이 지난 경우 (예: 3일 이상)
        if (today.isAfter(startDate.plusDays(2))) {
            throw new IllegalArgumentException("체크인 기간이 지났습니다");
        }
    }

    private void validateCheckOut(Reservation reservation) {
        // 체크인되지 않은 예약은 체크아웃 불가
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            if (reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
                throw new IllegalStateException("이미 체크아웃된 예약입니다");
            }
            throw new IllegalStateException("체크인하지 않은 예약은 체크아웃할 수 없습니다");
        }
    }
}
