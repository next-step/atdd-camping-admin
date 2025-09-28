package com.camping.admin.domain.enums;

import lombok.Getter;
import org.thymeleaf.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ReservationStatus {
    WAITING("WAITING"),
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    REJECTED("REJECTED"),
    CHECKED_IN("CHECKED_IN"),
    CHECKED_OUT("CHECKED_OUT"),
    CANCELED("CANCELED");

    private final String code;

    ReservationStatus(String code) {
        this.code = code;
    }

    public static ReservationStatus fromCode(String code) {
        for (ReservationStatus status : values()) if (StringUtils.equals(status.code, code)) return status;
        throw new IllegalArgumentException("올바른 상태를 입력해 주세요.");
    }
}