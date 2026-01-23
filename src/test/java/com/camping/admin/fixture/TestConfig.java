package com.camping.admin.fixture;

public class TestConfig {

    // 서버 설정
    public static final String BASE_URL = "http://localhost:8080";

    // 관리자 계정
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin123";

    // 테스트 데이터 ID (data.sql 기준)
    public static final class ProductIds {
        public static final Long LANTERN = 1L;           // 랜턴 (RENTAL)
        public static final Long FIREWOOD = 2L;          // 장작팩 (SALE)
        public static final Long COOKWARE_SET = 3L;      // 코펠 세트 (RENTAL)
        public static final Long CHAIR = 4L;             // 의자 (RENTAL)
        public static final Long TABLE = 5L;             // 테이블 (RENTAL)
        public static final Long BURNER = 6L;            // 버너 (RENTAL)
        public static final Long NOT_EXIST = 99999L;     // 존재하지 않는 상품

        private ProductIds() {}
    }

    public static final class ReservationIds {
        public static final Long HONG_RESERVATION = 1L;  // 홍길동 예약
        public static final Long KIM_RESERVATION = 2L;   // 김철수 예약

        private ReservationIds() {}
    }

    public static final class RentalRecordIds {
        public static final Long NOT_RETURNED = 1L;      // 반납 안 된 대여 기록
        public static final Long ALREADY_RETURNED = 2L;  // 이미 반납된 대여 기록
        public static final Long NOT_EXIST = 99999L;     // 존재하지 않는 대여 기록

        private RentalRecordIds() {}
    }

    private TestConfig() {}
}
