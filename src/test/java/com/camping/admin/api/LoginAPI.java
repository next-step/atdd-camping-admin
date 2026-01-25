package com.camping.admin.api;

import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * 로그인 관련 Step 정의
 *
 * Background에서 "관리자로 로그인되어 있다"를 처리하고,
 * 발급받은 토큰을 TestContext에 저장합니다.
 */
public class LoginAPI {

    @Autowired
    private TestContext testContext;  // 공유 저장소

    /**
     * 관리자 로그인 수행
     * POST /auth/login
     *
     * 발급받은 JWT 토큰을 TestContext에 저장하여
     * 다른 Step/API 클래스에서 사용할 수 있게 합니다.
     */
    @Given("관리자로 로그인되어 있다")
    public void 관리자_로그인() {
        Map<String, String> loginRequest = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        ExtractableResponse<Response> loginResponse = RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(loginRequest)
                .when()
                    .post("/auth/login")
                .then()
                    .extract();

        // TestContext에 토큰 저장 → 다른 클래스에서 사용 가능
        testContext.setAccessToken(loginResponse.jsonPath().getString("accessToken"));
    }
}
