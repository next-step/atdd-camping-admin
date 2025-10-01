package com.camping.admin.domain.constants;

import java.math.BigDecimal;

public final class BusinessConstants {

    private BusinessConstants() {
        // Utility class
    }

    public static final BigDecimal RESERVATION_DAILY_RATE = new BigDecimal("50000");
    public static final long MINIMUM_NIGHTS = 1L;
    public static final int FIRST_PAGE = 0;

    // Display text constants
    public static final String SALES_ITEM_SUFFIX = " 외";
    public static final String RESERVATION_PREFIX = "예약 #";
    public static final String RENTAL_RESERVATION_SUFFIX = " (예약#";
    public static final String PARENTHESIS_CLOSE = ")";
}