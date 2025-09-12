package com.camping.admin.hooks;

import com.camping.admin.steps.AuthSteps;
import com.camping.admin.support.TestContext;
import io.cucumber.java.BeforeAll;

public class TokenHook {
    public static TestContext testContext;

    @BeforeAll
    public static void initTokens() {
        testContext = new TestContext();
        AuthSteps authSteps = new AuthSteps();

        authSteps.login("관리자");
    }
}
