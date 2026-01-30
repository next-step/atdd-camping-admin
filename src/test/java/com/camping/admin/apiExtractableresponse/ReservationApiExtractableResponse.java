package com.camping.admin.apiExtractableresponse;

import com.camping.admin.common.CommonHooks;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ReservationApiExtractableResponse {

    public static Response 예약의_상태를_변경한다(Long id, String status) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + CommonHooks.adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"" + status + "\"}")
                .when()
                .patch("/admin/reservations/" + id + "/status");
    }

}
