package com.camping.admin.exception;

public class DuplicateSiteNumberException extends RuntimeException {
    public DuplicateSiteNumberException(String siteNumber) {
        super(String.format("이미 존재하는 사이트 번호입니다: %s", siteNumber));
    }
}
