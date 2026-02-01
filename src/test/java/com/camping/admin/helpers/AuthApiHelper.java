package com.camping.admin.helpers;

import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthApiHelper extends ApiClientBase {

    public Response login(String username, String password) {
        return unauthenticated()
            .body(Map.of("username", username, "password", password))
            .post("/auth/login");
    }

    public String getAdminToken() {
        var response = login("admin", "admin123");

        if (response.statusCode() != 200) {
            throw new RuntimeException("Login failed: " + response.statusCode()
                + " - " + response.body().asString());
        }

        var token = response.jsonPath().getString("accessToken");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token not found: " + response.body().asString());
        }

        return token;
    }
}
