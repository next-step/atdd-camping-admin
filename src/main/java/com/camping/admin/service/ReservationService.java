package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.dto.CreateReservationRequest;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.repository.CampsiteRepository;
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
    private final CampsiteRepository campsiteRepository;

    public List<ReservationResponse> findAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        Campsite campsite = campsiteRepository.findById(request.getCampsiteId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + request.getCampsiteId()));

        // 캠프사이트 상태 검증
        campsite.validateAvailability();

        // 날짜 중복 검증
        if (!campsite.isAvailableForReservation(request.getStartDate(), request.getEndDate())) {
            throw new IllegalStateException("Campsite " + campsite.getSiteNumber() +
                    " is not available for the selected dates");
        }

        Reservation reservation = new Reservation(
                request.getCustomerName(),
                request.getStartDate(),
                request.getEndDate(),
                campsite,
                request.getPhoneNumber()
        );

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    @Transactional
    public ReservationResponse updateReservationStatus(Long reservationId, String status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));

        reservation.updateStatus(status);
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }
}