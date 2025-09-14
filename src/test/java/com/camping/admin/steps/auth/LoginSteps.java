package com.camping.admin.steps.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.steps.test_context.TestContext;
import io.cucumber.java.en.Given;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class LoginSteps {

    private static String AUTH_TOKEN_COOKIE = "AUTH_TOKEN";

    @Given("어드민으로 로그인하였다")
    public void 어드민으로_로그인하였다() {
        어드민으로_로그인한다();
        로그인이_성공한다();
        인증_토큰을_저장한다();
    }

    public void 어드민으로_로그인한다() {
        var 로그인_응답 = AuthClient.로그인_요청을_한다("admin", "admin123");
        TestContext.auth.로그인_응답(로그인_응답);
    }

    public void 로그인이_성공한다() {
        Objects.requireNonNull(TestContext.auth.로그인_응답());
        assertThat(TestContext.auth.로그인_응답().statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public void 인증_토큰을_저장한다() {
        Objects.requireNonNull(TestContext.auth.로그인_응답());
        var 인증_토큰 = TestContext.auth.로그인_응답().getCookie(AUTH_TOKEN_COOKIE);
        TestContext.auth.인증_토큰(인증_토큰);
    }
}
