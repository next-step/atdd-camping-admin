package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class authSteps {

    @Autowired private TestContext context;

    @When("올바른 자격증명으로 로그인한다")
    public void 올바른자격증명으로로그인한다() {
        context.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "admin", "password", "admin123"))
                .post("/auth/login");
    }

    @When("잘못된 자격증명으로 로그인한다")
    public void 잘못된자격증명으로로그인한다() {
        context.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "admin", "password", "wrong-password"))
                .post("/auth/login");
    }

    @When("아이디 없이 로그인한다")
    public void 아이디없이로그인한다() {
        context.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("password", "admin123"))
                .post("/auth/login");
    }

    @When("비밀번호 없이 로그인한다")
    public void 비밀번호없이로그인한다() {
        context.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "admin"))
                .post("/auth/login");
    }

    @When("빈 문자열 자격증명으로 로그인한다")
    public void 빈문자열자격증명으로로그인한다() {
        context.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "", "password", ""))
                .post("/auth/login");
    }

    @When("토큰 없이 관리 API를 요청한다")
    public void 토큰없이관리API를요청한다() {
        context.response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get("/admin/campsites");
    }

    @When("유효하지 않은 토큰으로 관리 API를 요청한다")
    public void 유효하지않은토큰으로관리API를요청한다() {
        context.response = RestAssured.given()
                .header("Authorization", "Bearer this-is-not-a-valid-token")
                .contentType(ContentType.JSON)
                .get("/admin/campsites");
    }

    @When("인증 쿠키로 관리 API를 요청한다")
    public void 인증쿠키로관리API를요청한다() {
        context.response = RestAssured.given()
                .cookie("AUTH_TOKEN", context.jwtToken)
                .contentType(ContentType.JSON)
                .get("/admin/campsites");
    }

    @Then("로그인에 성공한다")
    public void 로그인에성공한다() {
        context.response.then().statusCode(200);
    }

    @Then("로그인이 거부된다")
    public void 로그인이거부된다() {
        context.response.then().statusCode(401);
    }

    @Then("인증 오류가 반환된다")
    public void 인증오류가반환된다() {
        context.response.then().statusCode(401);
    }

    @Then("인증 요청이 거부된다")
    public void 인증요청이거부된다() {
        context.response.then().statusCode(400);
    }

    @And("JWT 토큰이 발급된다")
    public void JWT토큰이발급된다() {
        context.response.then().body("accessToken", notNullValue());
    }
}
