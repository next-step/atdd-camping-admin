package com.camping.admin.steps;

import com.camping.admin.support.AuthHelper;
import io.cucumber.java.en.Given;

public class AuthSteps {
    @Given("관리자가 로그인을 했다")
    public void 관리자가로그인을했다() {
        AuthHelper.loginAdmin();
    }
}
