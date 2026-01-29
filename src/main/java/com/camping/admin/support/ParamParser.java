package com.camping.admin.support;

import com.camping.admin.domain.enums.ProductType;

import java.math.BigDecimal;

public class ParamParser {

    private ParamParser() {
        // 유틸리티 클래스 인스턴스화 방지
    }

    public static Integer parseInteger(String value) {
        return parseInteger(value, null);
    }

    public static Integer parseInteger(String value, Integer defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static BigDecimal parseBigDecimal(String value) {
        return parseBigDecimal(value, null);
    }

    public static BigDecimal parseBigDecimal(String value, BigDecimal defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static ProductType parseProductType(String value) {
        return parseProductType(value, null);
    }

    public static ProductType parseProductType(String value, ProductType defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return ProductType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}

