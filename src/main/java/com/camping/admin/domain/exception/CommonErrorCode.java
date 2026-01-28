package com.camping.admin.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 공통 에러 코드
 * 대부분의 도메인에서 사용하는 패턴: "{도메인}은(는) {에러내용}"
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    REQUIRED("common.required", "{0}은(는) 필수입니다", HttpStatus.BAD_REQUEST),
    INVALID("common.invalid", "{0}은(는) 유효하지 않은 형식입니다: {1}", HttpStatus.BAD_REQUEST),
    NEGATIVE("common.negative", "{0}은(는) 0 이상이어야 합니다", HttpStatus.BAD_REQUEST),
    MIN_VALUE("common.min-value", "{0}은(는) {1} 이상이어야 합니다", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    public DomainException withDomain(String domain) {
        return new DomainException(this, domain);
    }

    public DomainException withDomain(String domain, Object... args) {
        Object[] allArgs = new Object[args.length + 1];
        allArgs[0] = domain;
        System.arraycopy(args, 0, allArgs, 1, args.length);
        return new DomainException(this, allArgs);
    }

    /**
     * 클래스 기반으로 도메인명 자동 매핑
     * messages.properties의 domain.{ClassName} 키로 한글 도메인명 조회
     */
    public DomainException forClass(Class<?> clazz) {
        return new DomainException(this, clazz);
    }

    public DomainException forClass(Class<?> clazz, Object... args) {
        return new DomainException(this, clazz, args);
    }
}