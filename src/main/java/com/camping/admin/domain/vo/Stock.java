package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.CommonErrorCode;
import com.camping.admin.domain.exception.StockErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    private Integer quantity;

    public Stock(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw CommonErrorCode.NEGATIVE.forClass(Stock.class);
        }
        this.quantity = quantity;
    }

    public static Stock zero() {
        return new Stock(0);
    }

    public Stock decrease(int amount) {
        if (amount <= 0) {
            throw CommonErrorCode.MIN_VALUE.withDomain("감소량", 1);
        }
        if (this.quantity < amount) {
            throw StockErrorCode.INSUFFICIENT.with(this.quantity, amount);
        }
        return new Stock(this.quantity - amount);
    }

    public Stock increase(int amount) {
        if (amount <= 0) {
            throw CommonErrorCode.MIN_VALUE.withDomain("증가량", 1);
        }
        return new Stock(this.quantity + amount);
    }

    public boolean hasEnough(int amount) {
        return this.quantity >= amount;
    }

    public boolean isEmpty() {
        return this.quantity == 0;
    }
}