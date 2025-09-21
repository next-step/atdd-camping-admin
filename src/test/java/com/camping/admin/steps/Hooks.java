package com.camping.admin.steps;

import com.camping.admin.helper.CommonContext;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;

import static com.camping.admin.helper.RequestSpecFactory.getRequestSpec;
import static com.camping.admin.helper.TokenFactory.getAdminToken;

/**
 * 비즈니스 로직에 영향을 주지 않는 공통 세팅 (예. 토큰 세팅)
 */
public class Hooks {

    @Before
    public static void beforeScenario() {
        CommonContext.requestSpec = getRequestSpec();
    }

    @BeforeAll
    public static void initAdminToken() {
        CommonContext.adminToken = getAdminToken();
    }
}
