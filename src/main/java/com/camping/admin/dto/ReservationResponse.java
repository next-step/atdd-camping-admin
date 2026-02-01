package com.camping.admin.dto;

import com.camping.admin.domain.entity.Reservation;

import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        String customerName,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String campsiteSiteNumber,
        LocalDate reservationDate
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getCustomerName(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus(),
                reservation.getCampsite().getSiteNumber(),
                reservation.getReservationDate());
    }
}
