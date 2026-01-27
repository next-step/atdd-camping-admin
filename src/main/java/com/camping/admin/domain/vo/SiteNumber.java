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
public class SiteNumber {

    private static final Pattern SITE_NUMBER_PATTERN = Pattern.compile("^[A-Z]-\\d{2}$");

    private String value;

    public SiteNumber(String value) {
        if (value == null || value.isBlank()) {
            throw CommonErrorCode.REQUIRED.forClass(SiteNumber.class);
        }
        String normalized = value.toUpperCase().trim();
        if (!SITE_NUMBER_PATTERN.matcher(normalized).matches()) {
            throw CommonErrorCode.INVALID.forClass(SiteNumber.class, value);
        }
        this.value = normalized;
    }

    @Override
    public String toString() {
        return value;
    }
}