package com.camping.admin.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class CommonSteps {

    private final CommonContext context;
    private final TestHelperContext helpers;

    public CommonSteps(CommonContext context, TestHelperContext helpers) {
        this.context = context;
        this.helpers = helpers;
    }

    @Before(order = 0)
    public void loginAndGetToken() {
        context.login("admin", "admin123");
        helpers.initialize(context.getRequestSpec());
        System.out.println("[Before] 관리자 로그인 완료, 토큰 발급됨.");
    }

    @Given("관리자가 로그인되어 있다")
    public void 관리자가로그인되어있다() {
        System.out.println("[Given] 관리자가 로그인되어 있다");
    }
}