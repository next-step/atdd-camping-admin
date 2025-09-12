package com.camping.admin.steps;

import com.camping.admin.helper.ApiHelper;
import com.camping.admin.helper.HttpMethod;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    @Before
    public void beforeScenario() {
        log.info("=== 시나리오 시작: DB 상태 초기화 ===");
        StepContext.setSpec();
        
        // DB 리셋 API 호출
        resetDatabase();
    }
    
    @After
    public void afterScenario() {
        log.info("=== 시나리오 완료: DB 정리됨 ===");
    }
    
    private void resetDatabase() {
        try {
            ExtractableResponse<Response> response = ApiHelper.createExtractableResponseWithAuthorization(HttpMethod.DELETE, "admin/test/reset");

            if (response.statusCode() == 200) {
                log.info("✅ DB 리셋 완료");
            } else {
                log.warn("⚠️ DB 리셋 실패: " + response.statusCode());
            }
        } catch (Exception e) {
            log.warn("⚠️ DB 리셋 중 오류: " + e.getMessage());
        }
    }

    @BeforeAll
    public static void initAccessToken() {
        log.info("로그인 시도중...");
        Map<String, String> params = Map.of("username", "admin", "password", "admin123");

        ExtractableResponse<Response> response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(params)
                .when()
                .post("http://localhost:8081/auth/login")
                .then()
                .statusCode(200)
                .extract();

        String accessToken = response.jsonPath().get("accessToken");
        StepContext.setAccessToken(accessToken);
        log.info("로그인 완료");
    }
}
