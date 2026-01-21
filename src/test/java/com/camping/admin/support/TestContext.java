package com.camping.admin.support;

import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
public class TestContext {

    private String authToken;
    private Response lastResponse;

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void reset() {
        this.authToken = null;
        this.lastResponse = null;
    }
}
