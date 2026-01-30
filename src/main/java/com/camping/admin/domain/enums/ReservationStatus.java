package com.camping.admin.domain.enums;

public enum ReservationStatus {
    WAITING(true),
    PENDING(true),
    CONFIRMED(false),
    REJECTED(false),
    CHECKED_IN(false),
    CHECKED_OUT(false),
    CANCELLED(false);


    private final boolean cancelable;

    ReservationStatus(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public boolean canCancel() {
        return this.cancelable;
    }


}