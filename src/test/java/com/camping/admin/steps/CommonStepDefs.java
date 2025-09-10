package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.Then;

public class CommonStepDefs {
    @Then("응답 상태코드는 {int}이다")
    public void 응답상태코드는이다(int statusCode) {
        CommonContext.lastResponse.then().statusCode(statusCode);
    }

    @Then("권한 없음으로 인해 실패한다")
    public void 권한없음으로인해실패한다() {
        CommonContext.lastResponse.then().statusCode(401);
    }

    @Then("잘못된 요청으로 인해 실패한다")
    public void 잘못된요청으로인해실패한다() {
        CommonContext.lastResponse.then().statusCode(400);
    }
}
