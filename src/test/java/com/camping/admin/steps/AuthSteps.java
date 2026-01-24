package com.camping.admin.steps;

import com.camping.admin.steps.context.TestContext;
import com.camping.admin.steps.support.TestDataFactory;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthSteps {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private TestContext testContext;

    @Before(order = 1)
    public void adminLoginHook() {
        testContext.setAdminToken(testDataFactory.createAdminToken());
    }
}
