package com.camping.admin.steps;

import io.restassured.response.Response;

/**
 * Cucumber 시나리오 내에서 Step 클래스 간 공유되는 상태를 관리
 * - 도메인을 넘나드는 시나리오에서 상태 공유 가능
 * - Cucumber DI를 통해 동일 시나리오 내 모든 Step에서 같은 인스턴스 사용
 */
public class ScenarioContext {

    // === 공통 응답 ===
    private Response lastResponse;

    // === 상품 관련 ===
    private int targetProductId;
    private int originalStock;
    private String createdProductName;

    // === 캠핑장 관련 ===
    private int targetCampsiteId;
    private String createdSiteNumber;

    // === 대여 관련 ===
    private int targetRentalId;

    // === 예약 관련 ===
    private int targetReservationId;

    // === Response ===

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }

    // === 상품 ===

    public int getTargetProductId() {
        return targetProductId;
    }

    public void setTargetProductId(int targetProductId) {
        this.targetProductId = targetProductId;
    }

    public int getOriginalStock() {
        return originalStock;
    }

    public void setOriginalStock(int originalStock) {
        this.originalStock = originalStock;
    }

    public String getCreatedProductName() {
        return createdProductName;
    }

    public void setCreatedProductName(String createdProductName) {
        this.createdProductName = createdProductName;
    }

    // === 캠핑장 ===

    public int getTargetCampsiteId() {
        return targetCampsiteId;
    }

    public void setTargetCampsiteId(int targetCampsiteId) {
        this.targetCampsiteId = targetCampsiteId;
    }

    public String getCreatedSiteNumber() {
        return createdSiteNumber;
    }

    public void setCreatedSiteNumber(String createdSiteNumber) {
        this.createdSiteNumber = createdSiteNumber;
    }

    // === 대여 ===

    public int getTargetRentalId() {
        return targetRentalId;
    }

    public void setTargetRentalId(int targetRentalId) {
        this.targetRentalId = targetRentalId;
    }

    // === 예약 ===

    public int getTargetReservationId() {
        return targetReservationId;
    }

    public void setTargetReservationId(int targetReservationId) {
        this.targetReservationId = targetReservationId;
    }
}
