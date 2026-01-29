package com.camping.admin.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class CustomerAPI {

    /**
     * 고객 상세 조회 API 호출
     * GET /admin/customers/{customerId}
     */
    public ExtractableResponse<Response> 고객_상세_조회(String token, Long customerId) {
        return RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/admin/customers/{customerId}", customerId)
                .then()
                    .extract();
    }
}
