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
public class PhoneNumber {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^01[016789]-?\\d{3,4}-?\\d{4}$");

    private String value;

    public PhoneNumber(String value) {
        if (value == null || value.isBlank()) {
            throw CommonErrorCode.REQUIRED.forClass(PhoneNumber.class);
        }
        String normalized = value.replaceAll("-", "");
        if (!PHONE_PATTERN.matcher(value).matches() && !normalized.matches("^01[016789]\\d{7,8}$")) {
            throw CommonErrorCode.INVALID.forClass(PhoneNumber.class, value);
        }
        this.value = normalized;
    }

    public String formatted() {
        if (value.length() == 11) {
            return value.substring(0, 3) + "-" + value.substring(3, 7) + "-" + value.substring(7);
        } else if (value.length() == 10) {
            return value.substring(0, 3) + "-" + value.substring(3, 6) + "-" + value.substring(6);
        }
        return value;
    }
}