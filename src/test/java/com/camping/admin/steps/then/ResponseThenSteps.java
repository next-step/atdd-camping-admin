package com.camping.admin.steps.then;

import com.camping.admin.support.SharedState;
import io.cucumber.java.en.Then;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class ResponseThenSteps {

    private final SharedState state;

    public ResponseThenSteps(SharedState state) {
        this.state = state;
    }

    @Then("요청이 성공한다")
    public void 요청이_성공한다() {
        int statusCode = state.getResponse().getStatusCode();
        if (statusCode != 200 && statusCode != 201) {
            throw new AssertionError("Expected 2xx but got " + statusCode);
        }
    }

    @Then("요청이 실패한다")
    public void 요청이_실패한다() {
        state.getResponse().then().statusCode(400);
    }

    @Then("리소스를 찾을 수 없다")
    public void 리소스를_찾을_수_없다() {
        state.getResponse().then().statusCode(404);
    }

    @Then("목록이 조회된다")
    public void 목록이_조회된다() {
        state.getResponse().then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }
}
