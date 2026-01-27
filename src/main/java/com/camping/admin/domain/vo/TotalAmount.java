package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.CommonErrorCode;
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
            throw CommonErrorCode.REQUIRED.withDomain("가격");
        }
        if (quantity < 1) {
            throw CommonErrorCode.MIN_VALUE.withDomain("수량", 1);
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}