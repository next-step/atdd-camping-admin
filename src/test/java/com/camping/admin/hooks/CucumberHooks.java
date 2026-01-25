package com.camping.admin.hooks;

import com.camping.admin.support.AuthHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class CucumberHooks {

    @Before(order = 0)
    public void setUp() {
        AuthHelper.clearToken();
        System.out.println(">>> [Before] 시나리오 시작");
    }

    @After(order = 0)
    public void tearDown() {
        AuthHelper.clearToken();
        System.out.println(">>> [After] 시나리오 종료, 토큰 초기화");
    }
}
