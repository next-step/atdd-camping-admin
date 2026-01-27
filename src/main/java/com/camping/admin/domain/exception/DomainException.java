package com.camping.admin.domain.exception;

import java.text.MessageFormat;
import lombok.Getter;

/**
 * 도메인 예외
 * ErrorCode와 함께 사용하여 일관된 예외 처리
 */
@Getter
public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Class<?> sourceClass;
    private final Object[] args;

    public DomainException(ErrorCode errorCode, Object... args) {
        super(formatMessage(errorCode.getDefaultMessage(), args));
        this.errorCode = errorCode;
        this.sourceClass = null;
        this.args = args;
    }

    public DomainException(ErrorCode errorCode, Class<?> sourceClass, Object... args) {
        super(formatMessage(errorCode.getDefaultMessage(), prependClassName(sourceClass, args)));
        this.errorCode = errorCode;
        this.sourceClass = sourceClass;
        this.args = args;
    }

    public String getCode() {
        return errorCode.getCode();
    }

    /**
     * 도메인명 조회를 위한 메시지 키 반환
     * 예: PhoneNumber -> "domain.PhoneNumber"
     */
    public String getDomainNameKey() {
        if (sourceClass == null) {
            return null;
        }
        return "domain." + sourceClass.getSimpleName();
    }

    private static Object[] prependClassName(Class<?> clazz, Object[] args) {
        Object[] allArgs = new Object[args.length + 1];
        allArgs[0] = clazz.getSimpleName();
        System.arraycopy(args, 0, allArgs, 1, args.length);
        return allArgs;
    }

    private static String formatMessage(String template, Object[] args) {
        if (args == null || args.length == 0) {
            return template;
        }
        return MessageFormat.format(template, args);
    }
}