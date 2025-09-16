package com.camping.admin.helper;

import static com.camping.admin.context.CommonContext.getRequestSpec;
import static io.restassured.RestAssured.given;

import com.camping.admin.context.CommonContext;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class CampSiteApiHelper {
    public static Map<String, Object> createCampsiteData(String siteNumber, String description, int maxPeople) {
        Map<String, Object> campsite = new HashMap<>();
        campsite.put("siteNumber", siteNumber);
        campsite.put("description", description);
        campsite.put("maxPeople", maxPeople);
        return campsite;
    }

    public static Response sendCampsiteCreationRequest(String siteNumber) {
        Map<String, Object> campsiteDetails = createCampsiteData(siteNumber, "default description", 4);
        return given()
                .spec(getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(campsiteDetails)
                .when()
                .post("/admin/campsites");
    }

    public static Long findCampsiteIdBySiteNumber(String siteNumber) {
        Response response = given()
                .spec(getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .when()
                .get("/admin/campsites");

        response.then().statusCode(200);

        return response.jsonPath().param("siteNumber", siteNumber)
                .getLong("find { it.siteNumber == siteNumber }.campsiteId");
    }

    public static Response sendCampsiteUpdateRequest(Long campsiteId, String siteNumber, String description,
                                                     int maxPeople) {
        Map<String, Object> campsiteDetails = new HashMap<>();
        campsiteDetails.put("siteNumber", siteNumber);
        campsiteDetails.put("description", description);
        campsiteDetails.put("maxPeople", maxPeople);

        return given()
                .spec(getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(campsiteDetails)
                .when()
                .put("/admin/campsites/" + campsiteId);
    }

    public static Response sendCampsiteDeletionRequest(Long campsiteId) {
        return given()
                .spec(getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .when()
                .delete("/admin/campsites/" + campsiteId);
    }


}
