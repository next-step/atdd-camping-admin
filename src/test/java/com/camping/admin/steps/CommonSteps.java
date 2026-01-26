package com.camping.admin.steps;

import com.camping.admin.client.AuthClient;
import com.camping.admin.utils.DatabaseCleaner;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonSteps {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestContext testContext;

    @Autowired
    private AuthClient authClient;

    @Before(order = 2)
    public void setUp() {
        databaseCleaner.execute();
    }

    @Given("관리자 로그인이 되어 있다")
    public void 관리자_로그인이_되어있다() {
        String authToken = authClient.관리자_로그인을_한다();
        testContext.setAuthToken(authToken);
    }
}
