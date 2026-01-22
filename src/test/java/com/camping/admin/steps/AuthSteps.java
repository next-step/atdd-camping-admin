package com.camping.admin.steps;

import com.camping.admin.security.JwtService;
import com.camping.admin.steps.context.TestContext;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AuthSteps {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TestContext testContext;

    @Value("${admin.username}")
    private String adminUsername;

    @Given("관리자가 로그인되어 있다")
    public void adminIsLoggedIn() {
        String token = jwtService.generateToken(adminUsername);
        testContext.setAdminToken(token);
    }
}
