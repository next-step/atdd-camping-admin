package com.camping.admin.steps;

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
        StepContext.setSpec();
    }

    /**
     * 각 시나리오가 끝난 후 HTTP API를 통해 데이터베이스를 초기화합니다.
     * 순수 RestAssured 환경에서 Spring Bean 주입 없이 동작합니다.
     */
    @After
    public void afterScenario() {
        try {
            log.info("🧹 시나리오 완료 - HTTP API를 통해 데이터베이스 초기화 시작");

            ExtractableResponse<Response> response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + StepContext.getAccessToken())
                    .body("{}")
                    .when()
                    .post("http://localhost:8081/admin/database/reset")
                    .then()
                    .extract();

            if (response.statusCode() == 200) {
                log.info("✅ 데이터베이스 초기화 완료 - 다음 시나리오 준비됨");
                String responseBody = response.body().asString();
                log.debug("DB 초기화 응답: {}", responseBody);
            } else {
                log.warn("⚠️ 데이터베이스 초기화 실패 - 상태코드: {}, 응답: {}",
                        response.statusCode(), response.body().asString());
            }
        } catch (Exception e) {
            log.error("❌ 데이터베이스 초기화 중 오류 발생: {}", e.getMessage());
            // 테스트를 중단하지 않고 계속 진행 (다음 시나리오는 여전히 실행되어야 함)
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
