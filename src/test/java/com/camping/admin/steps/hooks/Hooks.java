package com.camping.admin.steps.hooks;

import com.camping.admin.config.RequestSpecFactory;
import com.camping.admin.context.CommonContext;
import com.camping.admin.helper.BaseApiHelper;
import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void setUp() {
        CommonContext.setRequestSpec(RequestSpecFactory.create());
        String adminToken = BaseApiHelper.authenticateAndGetToken();
        CommonContext.setAdminToken(adminToken);
        BaseApiHelper.cleanupDatabase(adminToken);
    }
}
