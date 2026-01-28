package com.camping.admin.steps;

import com.camping.admin.common.TestContext;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

    // ===== 201 =====

    @Then("{word}이 등록된다")
    public void 이_등록된다(String resource) {
        TestContext.getLastResponse().then().statusCode(201);
    }

    @Then("{word}가 등록된다")
    public void 가_등록된다(String resource) {
        TestContext.getLastResponse().then().statusCode(201);
    }

    // ===== 200 =====

    @Then("{word}이 완료된다")
    public void 이_완료된다(String action) {
        TestContext.getLastResponse().then().statusCode(200);
    }

    @Then("{word}이 수정된다")
    public void 이_수정된다(String resource) {
        TestContext.getLastResponse().then().statusCode(200);
    }

    @Then("{word}가 수정된다")
    public void 가_수정된다(String resource) {
        TestContext.getLastResponse().then().statusCode(200);
    }

    @Then("{word} 목록이 반환된다")
    public void 목록이_반환된다(String resource) {
        TestContext.getLastResponse().then().statusCode(200);
    }

    // ===== 400+ =====

    @Then("{word}이 실패한다")
    public void 이_실패한다(String action) {
        assertThat(TestContext.getLastResponse().statusCode()).isGreaterThanOrEqualTo(400);
    }

    @Then("{word}가 실패한다")
    public void 가_실패한다(String action) {
        assertThat(TestContext.getLastResponse().statusCode()).isGreaterThanOrEqualTo(400);
    }
}