package com.camping.admin.steps;

import io.cucumber.java.en.Then;

import static com.camping.admin.hooks.TokenHook.context;
import static org.assertj.core.api.Assertions.assertThat;

public class ResponseSteps {
    @Then("{string} 메시지가 응답 된다.")
    public void checkErrorMessage(String expectedMessage) {
        var response = context.getResponse();
        assertThat(response.jsonPath().getString("message")).isEqualTo(expectedMessage);
    }
}
