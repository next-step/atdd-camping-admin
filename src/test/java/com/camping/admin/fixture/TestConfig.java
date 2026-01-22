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

        private ProductIds() {}
    }

    public static final class ReservationIds {
        public static final Long HONG_RESERVATION = 1L;  // 홍길동 예약
        public static final Long KIM_RESERVATION = 2L;   // 김철수 예약

        private ReservationIds() {}
    }

    private TestConfig() {}
}
