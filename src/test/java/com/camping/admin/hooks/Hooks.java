package com.camping.admin.hooks;

import com.camping.admin.CommonContext;
import com.camping.admin.client.AuthClient;
import com.camping.admin.http.RequestSpecFactory;
import io.cucumber.java.Before;

public class Hooks {
    private final AuthClient authClient = new AuthClient();

    @Before(order = 0)
    public void initBaseSpec() {
        if (CommonContext.baseSpec == null) {
            CommonContext.baseSpec = RequestSpecFactory.base();
        }
    }

    @Before(order = 1)
    public void adminLogin() {
        if (CommonContext.accessToken == null) {
            CommonContext.accessToken = authClient.login("admin", "admin123");
            CommonContext.requestSpec = RequestSpecFactory.withBearer(CommonContext.baseSpec, CommonContext.accessToken);
        }
    }
}
