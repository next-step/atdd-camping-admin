package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class CommonStepDefs {

    @Given("관리자가 로그인했다")
    public void 관리자가로그인했다() {
        // 토큰은 이미 Hooks에서 설정됨
    }

    @Then("응답 상태코드는 {int}이다")
    public void 응답상태코드는이다(int statusCode) {
        CommonContext.getLastResponse().then().statusCode(statusCode);
    }
}
