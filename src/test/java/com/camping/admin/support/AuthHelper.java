package com.camping.admin.support;

import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class AuthHelper {
    private static String cachedAdminToken;

    private AuthHelper() {}

    // 관리자 로그인(캐시)
    public static String loginAdmin() {
        if (cachedAdminToken != null && !cachedAdminToken.isBlank()) {
            CommonContext.setAdminToken(cachedAdminToken);
            return cachedAdminToken;
        }
        Response res = given().spec(RequestSpecFactory.create())
                .body(Map.of("username","admin","password","admin123"))
                .post("/auth/login")
                .then().extract().response();

        String token = res.jsonPath().getString("accessToken");
        if (token == null || token.isBlank()) token = res.getCookie("AUTH_TOKEN");
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("관리자 토큰을 가져오지 못했습니다. 로그인 응답 본문: " + res.asString());
        }
        cachedAdminToken = token;
        CommonContext.setAdminToken(token);
        return token;
    }

    // 로그인 해제(토큰 제거)
    public static void clearLogin() {
        cachedAdminToken = null;
        CommonContext.setAdminToken(null);
    }

    // 만료/위조 토큰 상태로 설정(엣지 시나리오 프리셋)
    public static void setExpired() {
        cachedAdminToken = "expired.invalid.token";
        CommonContext.setAdminToken(cachedAdminToken);
    }
}
