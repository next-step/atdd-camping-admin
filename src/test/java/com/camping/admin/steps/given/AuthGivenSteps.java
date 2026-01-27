package com.camping.admin.steps.given;

import com.camping.admin.support.AuthHelper;
import io.cucumber.java.en.Given;

public class AuthGivenSteps {

    @Given("관리자가 로그인했다")
    public void 관리자가_로그인했다() {
        AuthHelper.getAdminToken();
    }
}
