package com.camping.admin.domain.vo;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 프로덕션용 - 랜덤 확인 코드 생성
 */
@Component
public class RandomConfirmationCodeGenerator implements ConfirmationCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public ConfirmationCode generate() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return new ConfirmationCode(sb.toString());
    }
}