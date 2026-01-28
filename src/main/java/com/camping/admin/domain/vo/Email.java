package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.CommonErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw CommonErrorCode.REQUIRED.forClass(Email.class);
        }
        String normalized = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw CommonErrorCode.INVALID.forClass(Email.class, value);
        }
        this.value = normalized;
    }

    @Override
    public String toString() {
        return value;
    }
}