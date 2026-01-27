package com.camping.admin.exception;

import com.camping.admin.domain.enums.ReservationStatus;

public class InvalidStatusTransitionException extends IllegalStateException {
    public InvalidStatusTransitionException(ReservationStatus from, ReservationStatus to) {
        super(String.format("Cannot transition from %s to %s", from, to));
    }
}
