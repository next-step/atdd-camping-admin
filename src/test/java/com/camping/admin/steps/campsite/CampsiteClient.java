package com.camping.admin.steps.campsite;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import java.util.Map;

public class CampsiteClient {

    public static Response 전체_캠프사이트를_조회한다(String authToken) {
        return given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/admin/campsites")
            .andReturn();
    }

    public static Response 캠프사이트를_생성한다(String authToken, String siteNumber, String description,
        int maxPeople) {
        return given()
            .header("Authorization", "Bearer " + authToken)
            .body(Map.of(
                "siteNumber", siteNumber,
                "description", description,
                "maxPeople", maxPeople
            ))
            .when()
            .post("/admin/campsites")
            .andReturn();
    }

    public static Response 캠프사이트를_수정한다(String authToken, Long campsiteId, String siteNumber,
        String description,
        int maxPeople) {
        return given()
            .header("Authorization", "Bearer " + authToken)
            .body(Map.of(
                "siteNumber", siteNumber,
                "description", description,
                "maxPeople", maxPeople
            ))
            .when()
            .put("/admin/campsites/" + campsiteId)
            .andReturn();
    }
}
