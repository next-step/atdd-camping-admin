package com.camping.admin.steps.common;

import com.camping.admin.support.ApiClient;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonSteps {

    @Autowired
    private ApiClient apiClient;

    @Given("관리자 권한으로 인증되어 있다")
    public void 관리자_권한으로_인증되어_있다() {
        apiClient.initialize();
    }
}