package com.camping.admin.domain.enums;

import java.util.Arrays;

public enum ReservationStatus {
    WAITING("대기중"),
    PENDING("대기"),
    CONFIRMED("확정"),
    REJECTED("거절"),
    CHECKED_IN("체크인"),
    CHECKED_OUT("체크아웃"),
    CANCELLED("취소");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReservationStatus fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(status -> status.displayName.equals(displayName))
                .findFirst()
                .orElse(null);
    }
}