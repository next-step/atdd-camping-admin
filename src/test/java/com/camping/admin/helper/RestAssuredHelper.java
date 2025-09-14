package com.camping.admin.helper;

import com.camping.admin.steps.StepContext;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class RestAssuredHelper {

    private static final List<HttpMethodStrategy> strategies = List.of(
            new GetStrategy(),
            new PostStrategy(),
            new PutStrategy(),
            new PatchStrategy(),
            new DeleteStrategy()
    );

    public <T> ExtractableResponse<Response> execute(HttpMethod method, String url, T body, boolean needAuthorization) {
        RequestSpecification requestSpec = needAuthorization
                ? StepContext.getRequestSpecificationWithAccessToken()
                : StepContext.getRequestSpecification();

        for (HttpMethodStrategy strategy : strategies) {
            if (strategy.supports(method)) {
                return strategy.execute(requestSpec, url, body);
            }
        }

        throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }

    public <T> ExtractableResponse<Response> execute(HttpMethod method, String url, T body) {
        return execute(method, url, body, false);
    }

    public ExtractableResponse<Response> execute(HttpMethod method, String url) {
        return execute(method, url, null, false);
    }

    public ExtractableResponse<Response> execute(HttpMethod method, String url, boolean needAuthorization) {
        return execute(method, url, null, needAuthorization);
    }

    public <T> ExtractableResponse<Response> get(String url, T body, boolean needAuthorization) {
        return execute(HttpMethod.GET, url, body, needAuthorization);
    }

    public <T> ExtractableResponse<Response> get(String url, T body) {
        return execute(HttpMethod.GET, url, body);
    }

    public ExtractableResponse<Response> get(String url) {
        return execute(HttpMethod.GET, url);
    }

    public ExtractableResponse<Response> get(String url, boolean needAuthorization) {
        return execute(HttpMethod.GET, url, needAuthorization);
    }

    public <T> ExtractableResponse<Response> post(String url, T body, boolean needAuthorization) {
        return execute(HttpMethod.POST, url, body, needAuthorization);
    }

    public <T> ExtractableResponse<Response> post(String url, T body) {
        return execute(HttpMethod.POST, url, body);
    }

    public ExtractableResponse<Response> post(String url) {
        return execute(HttpMethod.POST, url);
    }

    public ExtractableResponse<Response> post(String url, boolean needAuthorization) {
        return execute(HttpMethod.POST, url, needAuthorization);
    }

    public <T> ExtractableResponse<Response> put(String url, T body, boolean needAuthorization) {
        return execute(HttpMethod.PUT, url, body, needAuthorization);
    }

    public <T> ExtractableResponse<Response> put(String url, T body) {
        return execute(HttpMethod.PUT, url, body);
    }

    public ExtractableResponse<Response> put(String url) {
        return execute(HttpMethod.PUT, url);
    }

    public ExtractableResponse<Response> put(String url, boolean needAuthorization) {
        return execute(HttpMethod.PUT, url, needAuthorization);
    }

    public <T> ExtractableResponse<Response> patch(String url, T body, boolean needAuthorization) {
        return execute(HttpMethod.PATCH, url, body, needAuthorization);
    }

    public <T> ExtractableResponse<Response> patch(String url, T body) {
        return execute(HttpMethod.PATCH, url, body);
    }

    public ExtractableResponse<Response> patch(String url) {
        return execute(HttpMethod.PATCH, url);
    }

    public ExtractableResponse<Response> patch(String url, boolean needAuthorization) {
        return execute(HttpMethod.PATCH, url, needAuthorization);
    }

    public <T> ExtractableResponse<Response> delete(String url, T body, boolean needAuthorization) {
        return execute(HttpMethod.DELETE, url, body, needAuthorization);
    }

    public <T> ExtractableResponse<Response> delete(String url, T body) {
        return execute(HttpMethod.DELETE, url, body);
    }

    public ExtractableResponse<Response> delete(String url) {
        return execute(HttpMethod.DELETE, url);
    }

    public ExtractableResponse<Response> delete(String url, boolean needAuthorization) {
        return execute(HttpMethod.DELETE, url, needAuthorization);
    }
}