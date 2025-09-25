package com.camping.admin.support.hook;

import com.camping.admin.support.api.AuthApi;
import com.camping.admin.support.context.World;
import io.cucumber.java.Before;

public class AuthHook {
    private final World world;
    private final AuthApi authApi;

    public AuthHook(World world, AuthApi authApi) {
        this.world = world;
        this.authApi = authApi;
    }

    @Before
    public void login() {
        world.authToken = authApi.loginAndGetCookieToken("admin", "admin123");
    }
}
