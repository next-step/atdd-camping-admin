package com.camping.admin;

import com.camping.admin.helpers.AuthHelper;
import com.camping.admin.helpers.ContextHelper;
import io.cucumber.java.Before;

public class Hooks {
    @Before
    public void beforeScenario() {
        AuthHelper.clearTokens();
        ContextHelper.clearContext();
        AuthHelper.performAdminLogin();
    }
}
