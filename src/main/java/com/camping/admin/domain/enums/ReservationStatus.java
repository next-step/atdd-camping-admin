package com.camping.admin.domain.enums;

public enum ReservationStatus {
    WAITING(false),
    PENDING(false),
    CONFIRMED(false),
    REJECTED(true),
    CHECKED_IN(false),
    CHECKED_OUT(true),
    CANCELLED(true);

    private final boolean isFinal;

    ReservationStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}