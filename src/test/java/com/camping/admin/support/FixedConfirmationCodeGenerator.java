package com.camping.admin.support;

import com.camping.admin.domain.vo.ConfirmationCode;
import com.camping.admin.domain.vo.ConfirmationCodeGenerator;

/**
 * 테스트용 - 고정된 확인 코드 반환
 */
public class FixedConfirmationCodeGenerator implements ConfirmationCodeGenerator {

    private final String fixedCode;

    public FixedConfirmationCodeGenerator(String fixedCode) {
        this.fixedCode = fixedCode;
    }

    @Override
    public ConfirmationCode generate() {
        return new ConfirmationCode(fixedCode);
    }
}