package com.camping.admin.steps;

import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

import static com.camping.admin.hooks.TokenHook.testContext;

public class ResponseSteps {
    @Then("{int} 코드로 응답 한다.")
    public void failWithStatusCode(int statusCode) {
        var response = testContext.getResponse();
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @Then("{string} 메시지가 응답 한다.")
    public void responseMessage(String message) {
        var response = testContext.getResponse();
        assertThat(response.jsonPath().getString("message")).isEqualTo(message);
    }
}
