package com.camping.admin.domain.enums;

public enum ProductType {
    SALE(false, true),
    RENTAL(true, false);

    private final boolean rentable;
    private final boolean sellable;

    ProductType(boolean rentable, boolean sellable) {
        this.rentable = rentable;
        this.sellable = sellable;
    }

    public boolean isRentable() {
        return rentable;
    }

    public boolean isSellable() {
        return sellable;
    }
}