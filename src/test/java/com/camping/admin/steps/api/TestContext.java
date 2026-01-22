package com.camping.admin.steps.api;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

/**
 * 시나리오 내에서 Step 클래스 간 데이터를 공유하는 저장소
 *
 * @Component: Spring 빈으로 등록
 * @ScenarioScope: 시나리오마다 새 인스턴스 생성 (테스트 격리 보장)
 */
@Component
@ScenarioScope
public class TestContext {

    private String accessToken;
    private Long reservationId;
    private ExtractableResponse<Response> response;


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public ExtractableResponse<Response> getResponse() {
        return response;
    }

    public void setResponse(ExtractableResponse<Response> response) {
        this.response = response;
    }
}
