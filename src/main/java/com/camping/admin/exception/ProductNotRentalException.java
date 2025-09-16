package com.camping.admin.exception;

public class ProductNotRentalException extends RuntimeException {
    public ProductNotRentalException(String message) {
        super(message);
    }
}