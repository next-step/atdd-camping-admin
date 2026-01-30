package com.camping.admin.api;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * 시나리오 내에서 Step 클래스 간 데이터를 공유하는 저장소
 *
 * @Component: Spring 빈으로 등록
 * @ScenarioScope: 시나리오마다 새 인스턴스 생성 (테스트 격리 보장)
 */
@Getter
@Component
@ScenarioScope
public class TestContext {

    // 공통 데이터
    @Setter
    private String accessToken;

    @Setter
    private ExtractableResponse<Response> response;

    // 도메인별 데이터
    private final ReservationData reservation = new ReservationData();
    private final ProductData product = new ProductData();
    private final RentalData rental = new RentalData();
    private final CustomerData customer = new CustomerData();

    @Getter
    @Setter
    public static class ReservationData {
        private Long id;
        private String customerName;
        private String status;
    }

    @Getter
    @Setter
    public static class ProductData {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    public static class RentalData {
        private Long id;
    }

    @Getter
    @Setter
    public static class CustomerData {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
    }
}