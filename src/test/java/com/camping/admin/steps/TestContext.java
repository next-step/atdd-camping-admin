package com.camping.admin.steps;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestContext {
    private String adminToken;
    private Long reservationId;
    private Response response;
}
