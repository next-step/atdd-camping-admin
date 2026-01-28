package com.camping.admin.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * 도메인 에러 코드 공통 인터페이스
 */
public interface ErrorCode {

    String getCode();

    String getDefaultMessage();

    HttpStatus getHttpStatus();
}