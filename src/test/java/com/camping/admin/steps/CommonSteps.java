package com.camping.admin.steps;

import com.camping.admin.fixtures.ScenarioContext;
import com.camping.admin.helpers.AuthApiHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

    private final ScenarioContext context;
    private final AuthApiHelper authApi;

    public CommonSteps(ScenarioContext context, AuthApiHelper authApi) {
        this.context = context;
        this.authApi = authApi;
    }

    // === Given ===

    @Given("관리자가 로그인되어 있다")
    public void 관리자_로그인() {
        context.adminToken = authApi.getAdminToken();
    }

    @Given("인증되지 않은 사용자가")
    public void 비인증_사용자() {
        context.adminToken = null;
    }

    // === Then ===

    @And("HTTP 상태 코드는 {int}이다")
    public void HTTP_상태_검증(int expectedCode) {
        assertThat(context.마지막_응답_상태코드())
            .as("HTTP 상태 코드")
            .isEqualTo(expectedCode);
    }

    @And("{string} 오류가 발생한다")
    public void 오류_메시지_검증(String expectedMessage) {
        assertThat(context.마지막_응답_메시지())
            .as("오류 메시지")
            .isEqualTo(expectedMessage);
    }
}