package com.camping.admin.steps;

import com.camping.admin.support.TestContext;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

    private TestContext context = TestContext.getInstance();

    @Then("에러가 발생한다")
    public void 에러가_발생한다() {
        int statusCode = context.getLastResponse().statusCode();
        assertThat(statusCode).isGreaterThanOrEqualTo(400);
    }

    @Then("{int} 에러가 발생한다")
    public void 특정_에러가_발생한다(int expectedStatusCode) {
        context.getLastResponse().then()
                .statusCode(expectedStatusCode);
    }

    @Then("Not Found 에러가 발생한다")
    public void not_found_에러가_발생한다() {
        context.getLastResponse().then()
                .statusCode(404);
    }

    @Then("Bad Request 에러가 발생한다")
    public void bad_request_에러가_발생한다() {
        context.getLastResponse().then()
                .statusCode(400);
    }


}
