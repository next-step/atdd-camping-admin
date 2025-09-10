package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import com.camping.admin.support.RequestSpecFactory;
import com.camping.admin.support.TestDataFactory;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;

public class Hooks {
    
    @BeforeAll
    public static void initTokens() {
        String adminToken = TestDataFactory.generateAdminToken();
        CommonContext.adminToken = adminToken;
    }
    
    @Before
    public void beforeScenario() {
        CommonContext.requestSpec = RequestSpecFactory.create();
    }
}
