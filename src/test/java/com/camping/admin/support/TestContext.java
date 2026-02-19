package com.camping.admin.support;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class TestContext {

    public Response response;
    public String jwtToken;

    public Long campsiteId;
    public Long reservationId;
    public Long productId;
    public Long productId2;
    public Long rentalRecordId;

    public Integer productStockBefore;
    public Integer productStockBefore2;

    public RequestSpecification authRequest() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON);
    }
}
