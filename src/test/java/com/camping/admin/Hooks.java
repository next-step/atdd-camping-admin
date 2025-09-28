package com.camping.admin;

import com.camping.admin.helpers.AuthHelper;
import com.camping.admin.helpers.ContextHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {
    @Before
    public void beforeScenario() {
        ContextHelper.clearContext();
        AuthHelper.performAdminLogin();
    }

    @After
    public void afterScenario() {
        AuthHelper.clearTokens();
        ContextHelper.clearContext();
    }
}
