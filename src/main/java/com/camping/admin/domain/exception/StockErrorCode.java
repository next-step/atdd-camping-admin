package com.camping.admin.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 재고 관련 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum StockErrorCode implements ErrorCode {

    INSUFFICIENT("stock.insufficient", "재고가 부족합니다. 현재: {0}, 요청: {1}", HttpStatus.CONFLICT);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    public DomainException with(int current, int requested) {
        return new DomainException(this, current, requested);
    }
}