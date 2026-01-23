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
public class CampsiteApi {

    public ExtractableResponse<Response> 캠프사이트_생성_요청(String accessToken, String siteNumber, String description, Integer maxPeople) {
        Map<String, Object> params = new HashMap<>();
        params.put("siteNumber", siteNumber);
        params.put("description", description);
        params.put("maxPeople", maxPeople);

        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/campsites")
                .then().log().all()
                .extract();
    }
}
