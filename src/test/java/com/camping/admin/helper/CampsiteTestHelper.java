package com.camping.admin.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

/**
 * 캠핑장 도메인 테스트 헬퍼
 */
public class CampsiteTestHelper {

    // 테스트용 고정 캠핑장 ID (data.sql 기준)
    public static final int CAMPSITE_A01_ID = 1001;  // A-01
    public static final int CAMPSITE_A02_ID = 1002;  // A-02

    private final RequestSpecification requestSpec;

    public CampsiteTestHelper(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    // === API 호출 ===

    public Response getCampsites() {
        return RestAssured.given()
            .spec(requestSpec)
            .when()
            .get("/admin/campsites");
    }

    public Response createCampsite(String siteNumber, String description, int maxPeople) {
        return RestAssured.given()
            .spec(requestSpec)
            .body(Map.of(
                "siteNumber", siteNumber,
                "description", description,
                "maxPeople", maxPeople
            ))
            .when()
            .post("/admin/campsites");
    }

    public Response updateCampsite(int campsiteId, String description, int maxPeople) {
        return RestAssured.given()
            .spec(requestSpec)
            .body(Map.of(
                "description", description,
                "maxPeople", maxPeople
            ))
            .when()
            .put("/admin/campsites/" + campsiteId);
    }

    // === 조회 유틸리티 ===

    public int getFirstCampsiteId() {
        Response response = getCampsites();
        return response.jsonPath().getInt("[0].id");
    }

    public String getCampsiteSiteNumber(int campsiteId) {
        Response response = getCampsites();
        return response.jsonPath()
            .getString("find { it.id == " + campsiteId + " }.siteNumber");
    }
}