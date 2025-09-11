package com.camping.admin.steps.auth;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.restassured.response.Response;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class LoginSteps {

    @Getter
    private static String 어드민_인증_토큰;

    private Response 로그인_응답;

    @Given("어드민으로 로그인하였다")
    public void 어드민으로_로그인하였다() {
        어드민으로_로그인한다();
        로그인이_성공한다();
        인증_토큰을_저장한다();
    }

    public void 어드민으로_로그인한다() {
        var 로그인_응답 = given()
            .body(Map.of(
                "username", "admin",
                "password", "admin123"
            ))
            .when()
            .post("/auth/login")
            .andReturn();
        this.로그인_응답 = 로그인_응답;
    }

    public void 로그인이_성공한다() {
        Objects.requireNonNull(this.로그인_응답);
        assertThat(this.로그인_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public void 인증_토큰을_저장한다() {
        Objects.requireNonNull(this.로그인_응답);
        어드민_인증_토큰 = this.로그인_응답.getCookie("AUTH_TOKEN");
    }
}
