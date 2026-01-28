package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.CommonErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmationCode {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private String value;

    public ConfirmationCode(String value) {
        if (value == null || value.isBlank()) {
            throw CommonErrorCode.REQUIRED.forClass(ConfirmationCode.class);
        }
        String normalized = value.toUpperCase().trim();
        if (!CODE_PATTERN.matcher(normalized).matches()) {
            throw CommonErrorCode.INVALID.forClass(ConfirmationCode.class, value);
        }
        this.value = normalized;
    }

    public static ConfirmationCode generate() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return new ConfirmationCode(sb.toString());
    }

    @Override
    public String toString() {
        return value;
    }
}