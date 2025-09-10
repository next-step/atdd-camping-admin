package com.camping.admin.dto;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationResponse {
    private final Long id;
    private final String customerName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ReservationStatus status;
    private final String campsiteSiteNumber;
    private final LocalDate reservationDate;

    private ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.customerName = reservation.getCustomerName();
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.status = reservation.getStatus();
        this.campsiteSiteNumber = reservation.getCampsite().getSiteNumber();
        this.reservationDate = reservation.getReservationDate();
    }

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation);
    }
}
