package com.camping.admin.fixtures;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 시나리오 내 Step 간 상태를 공유하는 컨텍스트.
 * 각 시나리오마다 새로운 인스턴스가 생성되어 테스트 격리 보장.
 */
@Component
@ScenarioScope
public class ScenarioContext {

    // === 인증 ===
    public String adminToken;

    // === 도메인별 현재 작업중인 ID ===
    public Long currentReservationId;
    public Long currentCampsiteId;
    public Long currentProductId;
    public Long currentRentalId;
    public Long currentSaleId;

    // === API 응답 ===
    public Response lastResponse;
    public List<Response> concurrentResponses;

    // === 유틸리티 메서드 ===
    public int 마지막_응답_상태코드() {
        return lastResponse.statusCode();
    }

    public String 마지막_응답_메시지() {
        return lastResponse.jsonPath().getString("message");
    }

    public boolean 응답_성공여부() {
        var statusCode = lastResponse.statusCode();
        return statusCode >= 200 && statusCode < 300;
    }
}
