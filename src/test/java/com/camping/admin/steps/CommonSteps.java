package com.camping.admin.steps;

import com.camping.admin.fixture.TestConfig;
import com.camping.admin.support.AuthHelper;
import com.camping.admin.support.SharedState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class CommonSteps {

    private final SharedState state;

    public CommonSteps(SharedState state) {
        this.state = state;
    }

    // === 인증 ===
    @Given("관리자가 로그인했다")
    public void 관리자가_로그인했다() {
        AuthHelper.getAdminToken();
    }

    // === 상품 ID 설정 (여러 도메인에서 재사용) ===
    @Given("대여 가능한 상품이 존재한다")
    public void 대여_가능한_상품이_존재한다() {
        state.setProductId(TestConfig.ProductIds.LANTERN);
    }

    @Given("판매 가능한 상품이 존재한다")
    public void 판매_가능한_상품이_존재한다() {
        state.setProductId(TestConfig.ProductIds.FIREWOOD);
    }

    @Given("존재하지 않는 상품 ID를 사용한다")
    public void 존재하지_않는_상품_ID를_사용한다() {
        state.setProductId(TestConfig.ProductIds.NOT_EXIST);
    }

    // === 공통 응답 검증 ===
    @Then("요청이 성공한다")
    public void 요청이_성공한다() {
        int statusCode = state.getResponse().getStatusCode();
        if (statusCode != 200 && statusCode != 201) {
            throw new AssertionError("Expected 2xx but got " + statusCode);
        }
    }

    @Then("요청이 실패한다")
    public void 요청이_실패한다() {
        state.getResponse().then().statusCode(400);
    }

    @Then("리소스를 찾을 수 없다")
    public void 리소스를_찾을_수_없다() {
        state.getResponse().then().statusCode(404);
    }

    @Then("목록이 조회된다")
    public void 목록이_조회된다() {
        state.getResponse().then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }
}
