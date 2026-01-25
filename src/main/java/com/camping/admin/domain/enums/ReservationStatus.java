package com.camping.admin.domain.enums;

import java.util.Set;

public enum ReservationStatus {
    WAITING,
    PENDING,
    CONFIRMED(Set.of("CANCELLED", "CHECKED_IN")),
    REJECTED,
    CHECKED_IN(Set.of("CHECKED_OUT", "CANCELLED")),
    CHECKED_OUT,
    CANCELLED;

    private final Set<String> allowedTransitions;

    ReservationStatus() {
        this.allowedTransitions = Set.of();
    }

    ReservationStatus(Set<String> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(ReservationStatus newStatus) {
        return allowedTransitions.contains(newStatus.name());
    }
}