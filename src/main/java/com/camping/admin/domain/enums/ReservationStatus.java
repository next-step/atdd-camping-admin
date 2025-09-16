package com.camping.admin.domain.enums;

public enum ReservationStatus {
    WAITING,
    PENDING,
    CONFIRMED,
    REJECTED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED,
    NONE;

    public static ReservationStatus from(String status) {
        for (ReservationStatus rs : ReservationStatus.values()) {
            if (rs.name().equalsIgnoreCase(status)) {
                return rs;
            }
        }
        throw new IllegalArgumentException("Invalid reservation status: " + status);
    }

    public static boolean isCancelled(String status) {
        return status.equalsIgnoreCase(CANCELLED.name());
    }
}