package com.camping.admin.support;

import io.restassured.response.Response;

public class TestContext {

    private static TestContext instance;

    private AuthHelper authHelper;
    private Response lastResponse;

    public TestContext() {
        this.authHelper = new AuthHelper();
    }

    public static TestContext getInstance() {
        if (instance == null) {
            instance = new TestContext();
        }
        return instance;
    }

    public AuthHelper getAuthHelper() {
        return authHelper;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }
}
