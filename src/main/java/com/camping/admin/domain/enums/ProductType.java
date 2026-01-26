package com.camping.admin.domain.enums;

import java.util.Arrays;

public enum ProductType {
    SALE, // 판매용
    RENTAL // 대여용
    ;

    public static ProductType find(String productType) {
        return Arrays.stream(ProductType.values())
                .filter(p -> productType.toUpperCase().equals(p.name()))
                .findFirst()
                .orElse(SALE);
    }
}