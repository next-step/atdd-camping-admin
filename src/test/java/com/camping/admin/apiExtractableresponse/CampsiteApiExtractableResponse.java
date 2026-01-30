package com.camping.admin.apiExtractableresponse;

import com.camping.admin.common.CommonHooks;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class CampsiteApiExtractableResponse {

    public static Response 캠프사이트를_등록한다(Map<String, Object> campsiteData) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .body(campsiteData)
                .when()
                .post("/admin/campsites");
    }
}