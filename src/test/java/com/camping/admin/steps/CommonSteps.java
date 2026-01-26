package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.Then;

public class CommonSteps {

    private TestContext context = TestContext.getInstance();

    @Then("에러가 발생한다")
    public void 에러가_발생한다() {
        context.getLastResponse().then().statusCode(500);
    }




}
