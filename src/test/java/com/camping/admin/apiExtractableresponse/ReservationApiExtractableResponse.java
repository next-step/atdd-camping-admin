package com.camping.admin.apiExtractableresponse;

import com.camping.admin.common.CommonHooks;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class ReservationApiExtractableResponse {

    public static Response 예약을_생성한다(Map<String, Object> requestBody) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/admin/reservations");
    }

    public static Response 예약의_상태를_변경한다(Long id, String status) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"" + status + "\"}")
                .when()
                .patch("/admin/reservations/" + id + "/status");
    }

}
