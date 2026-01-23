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
public class RentalApi {

    public ExtractableResponse<Response> 대여_생성_요청(String accessToken, Long productId, int quantity) {
        return 대여_생성_요청(accessToken, productId, quantity, null);
    }

    public ExtractableResponse<Response> 대여_생성_요청(String accessToken, Long productId, int quantity, Long reservationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("quantity", quantity);
        if (reservationId != null) {
            params.put("reservationId", reservationId);
        }

        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/rentals")
                .then().log().all()
                .extract();
    }
}
