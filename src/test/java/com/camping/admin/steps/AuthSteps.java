package com.camping.admin.steps;

import com.camping.admin.api.TestContext;
import com.camping.admin.support.TestApiSupport;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private TestApiSupport api;

    @Given("관리자로 로그인되어 있다")
    public void 관리자_로그인() {
        var response = api.auth().로그인("admin", "admin123");
        testContext.setAccessToken(response.jsonPath().getString("accessToken"));
    }
}
