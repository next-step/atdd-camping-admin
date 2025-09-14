package com.camping.admin.domain.vo;

import com.camping.admin.exception.InvalidQuantityException;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class RecordQuantity {
    private int quantity;

    public RecordQuantity(int quantity) {
        validateRecordQuantity(quantity);
        this.quantity = quantity;
    }

    private void validateRecordQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero. Provided: " + quantity);
        }
    }

    public boolean isEqualTo(RecordQuantity other) {
        return this.quantity == other.quantity;
    }

    public boolean isGreaterThan(RecordQuantity other) {
        return this.quantity > other.quantity;
    }

    public RecordQuantity add(RecordQuantity other) {
        return new RecordQuantity(this.quantity + other.quantity);
    }

    public RecordQuantity subtract(RecordQuantity other) {
        return new RecordQuantity(this.quantity - other.quantity);
    }
}
