package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.Then;

public class CommonStepDefs {
    @Then("응답 상태코드는 {int}이다")
    public void 응답상태코드는이다(int statusCode) {
        CommonContext.getLastResponse().then().statusCode(statusCode);
    }
}
