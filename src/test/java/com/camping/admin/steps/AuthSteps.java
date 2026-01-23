package com.camping.admin.steps;

import com.camping.admin.steps.context.TestContext;
import com.camping.admin.steps.support.TestDataFactory;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthSteps {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private TestContext testContext;

    @Given("관리자가 로그인되어 있다")
    public void adminIsLoggedIn() {
        testContext.setAdminToken(testDataFactory.createAdminToken());
    }
}
