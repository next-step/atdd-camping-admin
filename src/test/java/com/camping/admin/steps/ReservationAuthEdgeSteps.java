package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import com.camping.admin.support.RequestSpecFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static com.camping.admin.steps.ReservationListSteps.lastResponse;

public class ReservationAuthEdgeSteps {
    private boolean asBrowser = false;

    @Given("관리자가 로그인하지 않았다.")
    public void 관리자가로그인하지않았다() {
        CommonContext.setAdminToken(null);   // 토큰 제거
        asBrowser = false;
    }

    @Given("로그인 유효기간이 지난 상태다.")
    public void 로그인유효기간이지난상태다() {
        // 유효하지 않은 토큰(임의 문자열이면 JwtAuthFilter에서 isTokenValid=false)
        CommonContext.setAdminToken("expired.invalid.token");
        asBrowser = false;
    }

    @And("브라우저로 본다.")
    public void 브라우저로본다() {
        asBrowser = true; // Accept: text/html을 쓰도록 플래그
    }

    @When("예약 목록을 조회했다.")
    public void 예약목록을조회했다() {
        var req = given().spec(RequestSpecFactory.create());
        String token = CommonContext.getAdminToken();
        if (token != null && !token.isBlank()) {
            req = req.header("Authorization", "Bearer " + token);
        }
        lastResponse = req.get("/admin/reservations");
    }

    @When("예약 목록 화면을 열었다.")
    public void 예약목록화면을열었다() {
        var req = given().spec(RequestSpecFactory.create());
        if (asBrowser) {
            req = req.accept(ContentType.HTML).redirects().follow(false); // text/html → 필터가 /login으로 리다이렉트
        }
        String token = CommonContext.getAdminToken();
        if (token != null && !token.isBlank()) {
            req = req.header("Authorization", "Bearer " + token);
        }
        lastResponse = req.get("/admin/reservations");
    }
}
