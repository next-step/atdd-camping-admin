package com.camping.admin.helper;

import static com.camping.admin.context.CommonContext.getRequestSpec;
import static io.restassured.RestAssured.given;

import com.camping.admin.context.CommonContext;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class TestApiHelper {

    public static String authenticateAndGetToken() {
        return given()
                .spec(getRequestSpec())
                .body(Map.of("username", "admin", "password", "admin123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .cookie("AUTH_TOKEN");
    }

    public static void cleanupDatabase(String token) {
        given().spec(getRequestSpec())
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/admin/reset-db")
                .then()
                .statusCode(200);
    }

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
}
