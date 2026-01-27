package com.camping.admin.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(Long reservationId) {
        super(String.format("Reservation not found ( id : %d )", reservationId));
    }
}
