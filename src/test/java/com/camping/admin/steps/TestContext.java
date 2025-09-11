package com.camping.admin.steps;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestContext {
    private String adminToken;
    private String userToken;
    private Long reservationId;
    private Response response;

    public void setTokenByRole(String role, String token) {
        if (role.equals("관리자")) {
            this.adminToken = token;
        } else {
            this.userToken = token;
        }
    }
}
