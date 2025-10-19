package com.camping.admin.support;

import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public final class HttpSupport {
    private HttpSupport() {}

    // 기본 JSON 요청 스펙
    public static RequestSpecification json() {
        return RequestSpecFactory.create();
    }

    // 브라우저처럼 보이게: text/html + 리다이렉트 미추종
    public static RequestSpecification browser() {
        return RequestSpecFactory.create()
                .accept(ContentType.HTML)
                .config(RestAssuredConfig.config() // 전역 레벨에서 리다이렉트 추종 금지
                        .redirect(RedirectConfig.redirectConfig()
                                .followRedirects(false))) // 상대경로 리다이렉트도 허용
                .redirects().follow(false); // 체이닝 레벨에서도 한 번 더
    }

    // GET + Bearer 자동 주입
    public static Response getWithAuth(String path) {
        String token = CommonContext.getAdminToken();
        return given().spec(json())
                .header("Authorization", "Bearer " + token)
                .get(path);
    }

    // GET (브라우저 모드), 토큰 없이, 쿠기 비우기
    public static Response getAsBrowserNoAuth(String path) {
        return given().spec(browser())
                .cookies(new Cookies()) // 기존 쿠키 전부 제거
                .header("Accept", "text/html") // 혹시 덮여있을 헤더를 확실히 세팅
                .get(path);
    }
}
