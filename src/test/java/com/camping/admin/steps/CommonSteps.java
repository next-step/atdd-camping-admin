package com.camping.admin.steps;

import io.cucumber.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonSteps {

    private static final Logger log = LoggerFactory.getLogger(CommonSteps.class);

    private final CommonContext context;
    private final ApiClientContext helpers;

    public CommonSteps(CommonContext context, ApiClientContext helpers) {
        this.context = context;
        this.helpers = helpers;
    }

    @Before(order = 0)
    public void loginAndGetToken() {
        context.login("admin", "admin123");
        helpers.initialize(context.getRequestSpec());
        log.info("[Before] 관리자 로그인 완료, 토큰 발급됨.");
    }
}