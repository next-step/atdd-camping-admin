package com.camping.admin.helpers;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class CampsiteTestHelper {

    private static final String CAMPSITE_ID_KEY = "campsiteId";

    public static Long getCampsiteId() {
        return ContextHelper.get(CAMPSITE_ID_KEY, Long.class);
    }

    public static void setCampsiteId(Long campsiteId) {
        ContextHelper.set(CAMPSITE_ID_KEY, campsiteId);
    }

    public static void setResponse(Response response) {
        ContextHelper.setResponse(response);
    }

    public static Response getLastResponse() {
        return ContextHelper.getLastResponse();
    }

    public static Response createCampsite(String siteNumber, String description, Integer maxPeople) {
        Map<String, Object> request = Map.of(
                "siteNumber", siteNumber,
                "description", description,
                "maxPeople", maxPeople
        );

        return ApiHelper.givenAuthenticated()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/admin/campsites");
    }

    public static Response createCampsiteWithoutSiteNumber(String description, Integer maxPeople) {
        Map<String, Object> request = Map.of(
                "description", description,
                "maxPeople", maxPeople
        );

        return ApiHelper.givenAuthenticated()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/admin/campsites");
    }

    public static Response getAllCampsites() {
        return ApiHelper.givenAuthenticated()
                .when()
                .get("/admin/campsites");
    }

    public static Response updateCampsite(Long campsiteId, String siteNumber, String description, Integer maxPeople) {
        Map<String, Object> request = Map.of(
                "siteNumber", siteNumber,
                "description", description,
                "maxPeople", maxPeople
        );

        return ApiHelper.givenAuthenticated()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/admin/campsites/" + campsiteId);
    }

    public static Long createAndGetCampsiteId(String siteNumber, String description, Integer maxPeople) {
        Response response = createCampsite(siteNumber, description, maxPeople);
        return response.jsonPath().getLong("id");
    }
}
