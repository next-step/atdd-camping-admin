package com.camping.admin.domain.enums;

public enum ReservationStatus {
    WAITING,
    PENDING,
    CONFIRMED,
    REJECTED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED;

    public static ReservationStatus from(String reservationStatus) {
        for (ReservationStatus status : ReservationStatus.values()) {
            if (status.name().equalsIgnoreCase(reservationStatus)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid reservation status: " + reservationStatus);
    }
}