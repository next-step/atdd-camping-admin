package com.camping.admin.domain.vo;

import com.camping.admin.exception.InsufficientStockException;
import com.camping.admin.exception.InvalidQuantityException;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class StockQuantity {
    private int quantity;

    public StockQuantity(int quantity) {
        validateStockQuantity(quantity);
        this.quantity = quantity;
    }

    public StockQuantity(Integer quantity) {
        validateStockQuantity(quantity);
        this.quantity = quantity;
    }

    private void validateStockQuantity(int quantity) {
        if (quantity < 0) {
            throw new InvalidQuantityException("Stock quantity cannot be negative. Provided: " + quantity);
        }
    }

    public boolean canDecrease(RecordQuantity decreaseAmount) {
        return this.quantity >= decreaseAmount.getQuantity();
    }

    public StockQuantity decrease(RecordQuantity decreaseAmount) {
        if (!canDecrease(decreaseAmount)) {
            throw new InsufficientStockException(
                    "Insufficient stock. Current: " + this.quantity + ", Required: " + decreaseAmount.getQuantity()
            );
        }
        return new StockQuantity(this.quantity - decreaseAmount.getQuantity());
    }

    public StockQuantity increase(RecordQuantity increaseAmount) {
        return new StockQuantity(this.quantity + increaseAmount.getQuantity());
    }

    public boolean isEmpty() {
        return this.quantity == 0;
    }

    public boolean isAvailable(RecordQuantity requiredAmount) {
        return this.quantity >= requiredAmount.getQuantity();
    }

    public StockQuantity add(StockQuantity other) {
        return new StockQuantity(this.quantity + other.quantity);
    }

    public boolean isEqualTo(StockQuantity other) {
        return this.quantity == other.quantity;
    }

    public boolean isGreaterThan(StockQuantity other) {
        return this.quantity > other.quantity;
    }

    public boolean isLessThan(StockQuantity other) {
        return this.quantity < other.quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockQuantity that = (StockQuantity) o;
        return quantity == that.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(quantity);
    }
}