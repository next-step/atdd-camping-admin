package com.camping.admin.domain.enums;

public enum ReservationStatus {
    WAITING,
    PENDING,
    CONFIRMED,
    REJECTED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED;

    public Boolean isCancelled() {
        return this == CANCELLED;
    }

    public static ReservationStatus getByString(String statusValue) {
        if (statusValue == null) {
            throw new IllegalArgumentException("예약 상태를 입력해주세요.");
        }
        try {
            return ReservationStatus.valueOf(statusValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("%s 예약 상태는 없습니다.", statusValue));
        }
    }
}
