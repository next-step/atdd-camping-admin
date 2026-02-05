package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.CreateReservationRequest;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.exception.DuplicateReservationException;
import com.camping.admin.exception.NotFoundException;
import com.camping.admin.repository.CampsiteRepository;
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
    private final CampsiteRepository campsiteRepository;

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse create(CreateReservationRequest request) {
        validateCreateRequest(request);

        Campsite campsite = campsiteRepository.findById(request.getCampsiteId())
                .orElseThrow(() -> new NotFoundException("Cannot find campsite with id: " + request.getCampsiteId()));

        // 중복 예약 체크
        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                request.getCampsiteId(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (!overlapping.isEmpty()) {
            throw new DuplicateReservationException("Reservation already exists for the given dates");
        }

        Reservation reservation = new Reservation(
                request.getCustomerName(),
                request.getStartDate(),
                request.getEndDate(),
                campsite
        );
        reservation.setPhoneNumber(request.getPhoneNumber());

        Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }

    private void validateCreateRequest(CreateReservationRequest request) {
        if (request.getCustomerName() == null || request.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        if (request.getCampsiteId() == null) {
            throw new IllegalArgumentException("Campsite ID is required");
        }
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
