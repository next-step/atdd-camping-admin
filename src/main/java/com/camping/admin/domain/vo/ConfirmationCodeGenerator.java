package com.camping.admin.domain.vo;

/**
 * 확인 코드 생성 전략 인터페이스
 * - 프로덕션: RandomConfirmationCodeGenerator
 * - 테스트: FixedConfirmationCodeGenerator
 */
public interface ConfirmationCodeGenerator {

    ConfirmationCode generate();
}