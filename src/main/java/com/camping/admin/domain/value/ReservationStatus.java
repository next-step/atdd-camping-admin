package com.camping.admin.domain.value;

public enum ReservationStatus {
    CONFIRMED("CONFIRMED"),
    CHECKED_IN("CHECKED_IN"),
    CHECKED_OUT("CHECKED_OUT"),
    CANCELLED("CANCELLED");

    private final String value;

    ReservationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isActive() {
        return this == CONFIRMED || this == CHECKED_IN;
    }

    public static ReservationStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return CONFIRMED;
        }

        for (ReservationStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown reservation status: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}