package com.camping.admin.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

/**
 * 캠핑장 도메인 API 클라이언트
 */
public class CampsiteApiClient {

    private final RequestSpecification requestSpec;

    public CampsiteApiClient(RequestSpecification requestSpec) {
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
}