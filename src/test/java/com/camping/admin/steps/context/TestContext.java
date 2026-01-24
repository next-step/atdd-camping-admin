package com.camping.admin.steps.context;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class TestContext {
    private String adminToken;
    private Long lastProductId;
    private Long lastReservationId;
    private ExtractableResponse<Response> lastResponse;

    public String getAdminToken() {
        return adminToken;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }

    public Long getLastProductId() {
        return lastProductId;
    }

    public void setLastProductId(Long lastProductId) {
        this.lastProductId = lastProductId;
    }

    public Long getLastReservationId() {
        return lastReservationId;
    }

    public void setLastReservationId(Long lastReservationId) {
        this.lastReservationId = lastReservationId;
    }

    public ExtractableResponse<Response> getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(ExtractableResponse<Response> lastResponse) {
        this.lastResponse = lastResponse;
    }
}
