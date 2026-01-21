package com.camping.admin.support;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ApiClient {

    @Autowired
    private Environment environment;

    @Autowired
    private TestContext testContext;

    private int getPort() {
        return Integer.parseInt(environment.getProperty("local.server.port", "8080"));
    }

    public Response get(String path) {
        return buildRequest()
                .get(path);
    }

    public Response get(String path, Map<String, ?> queryParams) {
        return buildRequest()
                .queryParams(queryParams)
                .get(path);
    }

    public Response post(String path, Object body) {
        return buildRequest()
                .body(body)
                .post(path);
    }

    public Response put(String path, Object body) {
        return buildRequest()
                .body(body)
                .put(path);
    }

    public Response patch(String path, Object body) {
        return buildRequest()
                .body(body)
                .patch(path);
    }

    public Response patch(String path) {
        return buildRequest()
                .patch(path);
    }

    public Response delete(String path) {
        return buildRequest()
                .delete(path);
    }

    private RequestSpecification buildRequest() {
        RequestSpecification spec = RestAssured.given()
                .baseUri("http://localhost")
                .port(getPort())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        String token = testContext.getAuthToken();
        if (token != null) {
            spec.header("Authorization", "Bearer " + token);
        }

        return spec;
    }
}
