package com.camping.admin.domain.vo;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TotalAmount {

    private BigDecimal value;

    private TotalAmount(BigDecimal value) {
        this.value = value;
    }

    public TotalAmount(BigDecimal price, int quantity) {
        this(calculateTotal(price, quantity));
    }

    private static BigDecimal calculateTotal(BigDecimal price, int quantity) {
        if (price == null) {
            throw new IllegalArgumentException("가격은 필수입니다");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}