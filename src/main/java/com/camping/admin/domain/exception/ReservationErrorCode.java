package com.camping.admin.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 예약 관련 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {

    ALREADY_FINAL("reservation.already-final", "이미 {0} 상태인 예약은 변경할 수 없습니다", HttpStatus.CONFLICT),
    INVALID_STATUS("reservation.invalid-status", "유효하지 않은 예약 상태입니다: {0}", HttpStatus.BAD_REQUEST),
    RESERVATION_DATE_AFTER_START("reservation.date-after-start", "예약일은 숙박 시작일보다 이전이어야 합니다", HttpStatus.BAD_REQUEST),
    END_DATE_BEFORE_START("reservation.end-before-start", "종료일은 시작일보다 이전일 수 없습니다", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    public DomainException with(Object... args) {
        return new DomainException(this, args);
    }
}