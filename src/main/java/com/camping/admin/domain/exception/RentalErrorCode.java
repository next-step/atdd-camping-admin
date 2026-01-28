package com.camping.admin.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 대여 관련 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum RentalErrorCode implements ErrorCode {

    ALREADY_RETURNED("rental.already-returned", "이미 반납된 대여 기록입니다", HttpStatus.CONFLICT);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    public DomainException toException() {
        return new DomainException(this);
    }
}