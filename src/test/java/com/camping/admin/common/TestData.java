package com.camping.admin.common;

public class TestData {

    // Products - RENTAL
    public static final Long PRODUCT_LANTERN_ID = 1L;       // 랜턴 (재고 20)
    public static final Long PRODUCT_COOKWARE_ID = 3L;      // 코펠 세트 (재고 15)
    public static final Long PRODUCT_TABLE_ID = 5L;         // 테이블 (재고 10)

    // Products - SALE (대여 불가)
    public static final Long PRODUCT_FIREWOOD_ID = 2L;      // 장작팩

    // Products - 존재하지 않는 ID
    public static final Long PRODUCT_NOT_FOUND_ID = 9999L;

    // Reservations
    public static final Long RESERVATION_HONG_ID = 1L;      // 홍길동
    public static final Long RESERVATION_KIM_ID = 2L;       // 김철수

    // Reservations - 존재하지 않는 ID
    public static final Long RESERVATION_NOT_FOUND_ID = 9999L;

    public static Long getProductId(String productName) {
        return switch (productName) {
            case "랜턴" -> PRODUCT_LANTERN_ID;
            case "코펠 세트" -> PRODUCT_COOKWARE_ID;
            case "테이블" -> PRODUCT_TABLE_ID;
            case "장작팩" -> PRODUCT_FIREWOOD_ID;
            default -> throw new IllegalArgumentException("Unknown product: " + productName);
        };
    }

    public static Long getReservationId(String customerName) {
        return switch (customerName) {
            case "홍길동" -> RESERVATION_HONG_ID;
            case "김철수" -> RESERVATION_KIM_ID;
            default -> throw new IllegalArgumentException("Unknown customer: " + customerName);
        };
    }
}
