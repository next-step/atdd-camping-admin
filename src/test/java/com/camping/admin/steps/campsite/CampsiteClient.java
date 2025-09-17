package com.camping.admin.steps.campsite;

import static io.restassured.RestAssured.given;

import com.camping.admin.steps.test_context.TestContext;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class CampsiteClient {

    public static Response 전체_캠프사이트_조회_요청을_한다() {
        return 전체_캠프사이트_조회_요청을_한다(TestContext.auth.인증_토큰());
    }

    public static Response 전체_캠프사이트_조회_요청을_한다(String authToken) {
        return given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/admin/campsites")
            .andReturn();
    }

    public static Response 캠프사이트_생성_요청을_한다(String siteNumber, String description, int maxPeople) {
        return 캠프사이트_생성_요청을_한다(TestContext.auth.인증_토큰(), siteNumber, description, maxPeople);
    }

    public static Response 캠프사이트_생성_요청을_한다(String authToken, String siteNumber, String description,
        int maxPeople) {
        return given()
            .header("Authorization", "Bearer " + authToken)
            .body(new HashMap<String, Object>() {{ // null 값을 허용하기 위해 HashMap 사용
                      put("siteNumber", siteNumber);
                      put("description", description);
                      put("maxPeople", maxPeople);
                  }}
            )
            .when()
            .post("/admin/campsites")
            .andReturn();
    }

    public static Response 캠프사이트_수정_요청을_한다(Long campsiteId, String siteNumber, String description, int maxPeople) {
        return 캠프사이트_수정_요청을_한다(TestContext.auth.인증_토큰(), campsiteId, siteNumber, description, maxPeople);
    }

    public static Response 캠프사이트_수정_요청을_한다(String authToken, Long campsiteId, String siteNumber,
        String description,
        int maxPeople) {
        return given()
            .header("Authorization", "Bearer " + authToken)
            .body(Map.of(
                "siteNumber", siteNumber,
                "description", description,
                "maxPeople", maxPeople
            ))
            .when()
            .put("/admin/campsites/" + campsiteId)
            .andReturn();
    }
}
