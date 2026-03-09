package com.camping.admin.dto;

import com.camping.admin.domain.entity.Reservation;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationResponse {
    private final Long id;
    private final String customerName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status;
    private final String campsiteSiteNumber;
    private final LocalDate reservationDate;

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation);
    }

    private ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.customerName = reservation.getCustomerName();
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.status = reservation.getStatus();
        this.campsiteSiteNumber = reservation.getCampsite().getSiteNumber();
        this.reservationDate = reservation.getReservationDate();
    }
}
