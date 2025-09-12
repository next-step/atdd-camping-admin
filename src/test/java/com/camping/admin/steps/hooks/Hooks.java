package com.camping.admin.steps.hooks;

import com.camping.admin.config.RequestSpecFactory;
import com.camping.admin.context.CommonContext;
import com.camping.admin.helper.TestApiHelper;
import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void setUp() {
        CommonContext.setRequestSpec(RequestSpecFactory.create());
        String adminToken = TestApiHelper.authenticateAndGetToken();
        CommonContext.setAdminToken(adminToken);
        TestApiHelper.cleanupDatabase(adminToken);
    }
}
