package com.camping.admin.steps.test_context;

import com.camping.admin.steps.test_context.auth.AuthTestContext;
import com.camping.admin.steps.test_context.campsite.CampsiteTestContext;

public class TestContext {

    public final static AuthTestContext auth = new AuthTestContext();
    public final static CampsiteTestContext campsite = new CampsiteTestContext();

    public static void clear() {
        auth.clear();
        campsite.clear();
    }

    private enum ContextKey {
        ADMIN_AUTH_TOKEN;
    }
}
