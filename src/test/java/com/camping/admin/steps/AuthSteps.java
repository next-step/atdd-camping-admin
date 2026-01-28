package com.camping.admin.steps;

import com.camping.admin.common.TestContext;
import io.cucumber.java.en.Given;

import static org.assertj.core.api.Assertions.*;

public class AuthSteps {

    @Given("관리자가 로그인되어 있다")
    public void 관리자가_로그인되어_있다() {
        assertThat(TestContext.getAdminToken()).isNotNull();
    }

    @Given("관리자가 로그인되어 있지 않다")
    public void 관리자가_로그인되어_있지_않다() {
        TestContext.setAdminToken(null);
    }
}
