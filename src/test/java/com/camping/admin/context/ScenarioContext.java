package com.camping.admin.context;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;

public class ScenarioContext {
    private static ExtractableResponse<Response> response;

    public static ExtractableResponse<Response> getResponse() {
        return response;
    }

    public static void setResponse(ExtractableResponse<Response> response) {
        ScenarioContext.response = response;
    }
}
