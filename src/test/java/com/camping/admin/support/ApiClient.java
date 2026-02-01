package com.camping.admin.support;

import com.camping.admin.dto.CreateProductRequest;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import com.camping.admin.security.JwtService;
import io.cucumber.spring.ScenarioScope;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ApiClient {

    private final JwtService jwtService;

    @LocalServerPort
    private int port;

    @Value("${admin.username}")
    private String adminUsername;

    private String adminToken;
    private boolean initialized = false;

    public ApiClient(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void initialize() {
        if (!initialized) {
            RestAssured.port = port;
            RestAssured.baseURI = "http://localhost";
            adminToken = jwtService.generateToken(adminUsername);
            initialized = true;
        }
    }

    public Response patch(String path, UpdateReservationStatusRequest body) {
        initialize();
        return RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch(path);
    }

    public Response get(String path) {
        initialize();
        return RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .get(path);
    }

    public Response post(String path, Object body) {
        initialize();
        return RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path);
    }
}