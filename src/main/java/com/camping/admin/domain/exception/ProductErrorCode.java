package com.camping.admin.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 상품 관련 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    NOT_RENTABLE("product.not-rentable", "대여 불가능한 상품입니다", HttpStatus.BAD_REQUEST),
    NOT_SELLABLE("product.not-sellable", "판매 불가능한 상품입니다", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    public DomainException toException() {
        return new DomainException(this);
    }
}