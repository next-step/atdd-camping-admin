package com.camping.admin.steps.support;

import com.camping.admin.domain.enums.ProductType;
import java.util.HashMap;
import java.util.Map;

public class ProductTypeMapper {
    private static final Map<String, ProductType> MAPPING = new HashMap<>();

    static {
        MAPPING.put("판매", ProductType.SALE);
        MAPPING.put("SALE", ProductType.SALE);
        MAPPING.put("대여", ProductType.RENTAL);
        MAPPING.put("RENTAL", ProductType.RENTAL);
    }

    public static ProductType from(String productTypeStr) {
        return MAPPING.entrySet().stream()
                .filter(entry -> productTypeStr.contains(entry.getKey()) || entry.getKey().equalsIgnoreCase(productTypeStr))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(ProductType.RENTAL);
    }
}
