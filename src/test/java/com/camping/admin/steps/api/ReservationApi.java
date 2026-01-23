package com.camping.admin.steps.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("NonAsciiCharacters")
@Component
public class ReservationApi {

    public ExtractableResponse<Response> 예약_상태_변경_요청(String accessToken, Long reservationId, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);

        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(params)
                .when().patch("/admin/reservations/" + reservationId + "/status")
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> 예약_목록_조회_요청(String accessToken) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/admin/reservations")
                .then().log().all()
                .extract();
    }
}