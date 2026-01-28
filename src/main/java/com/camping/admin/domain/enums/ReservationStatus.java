package com.camping.admin.domain.enums;

import java.util.Set;

public enum ReservationStatus {
    WAITING,
    PENDING,
    CONFIRMED,
    REJECTED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED;

    public boolean canTransitionTo(ReservationStatus nextStatus) {
        return switch (this) {
            case WAITING -> Set.of(PENDING, REJECTED, CANCELLED).contains(nextStatus);
            case PENDING -> Set.of(CONFIRMED, REJECTED, CANCELLED).contains(nextStatus);
            case CONFIRMED -> Set.of(CHECKED_IN, CANCELLED).contains(nextStatus);
            case CHECKED_IN -> Set.of(CHECKED_OUT, CANCELLED).contains(nextStatus);
            default -> false; // REJECTED, CHECKED_OUT, CANCELLED 의 경우 상태 전이 불가
        };
    }
}
