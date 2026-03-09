package com.camping.admin.service;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.repository.ReservationRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private static final Map<ReservationStatus, Set<ReservationStatus>> ALLOWED_TRANSITIONS = Map.of(
            ReservationStatus.CONFIRMED, Set.of(ReservationStatus.CHECKED_IN, ReservationStatus.CANCELLED),
            ReservationStatus.CHECKED_IN, Set.of(ReservationStatus.CHECKED_OUT)
    );

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse updateStatus(Long id, String statusValue) {
        if (statusValue == null || statusValue.isBlank()) {
            throw new IllegalArgumentException("상태 값은 필수입니다.");
        }

        ReservationStatus newStatus;
        try {
            newStatus = ReservationStatus.valueOf(statusValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 상태 값입니다: " + statusValue);
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + id));

        ReservationStatus currentStatus = ReservationStatus.valueOf(reservation.getStatus());

        if (currentStatus == newStatus) {
            throw new IllegalStateException("동일한 상태로 변경할 수 없습니다: " + newStatus);
        }

        Set<ReservationStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new IllegalStateException(
                    "허용되지 않는 상태 전이입니다: " + currentStatus + " → " + newStatus);
        }

        reservation.setStatus(newStatus.name());
        reservationRepository.save(reservation);
        return ReservationResponse.from(reservation);
    }
}
