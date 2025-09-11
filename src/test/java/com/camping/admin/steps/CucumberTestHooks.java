package com.camping.admin.steps;

import io.cucumber.java.BeforeAll;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CucumberTestHooks {

    @BeforeAll
    public static void setupRestAssured() {
        var adminApiUrl = "http://localhost:8080";

        RestAssured.requestSpecification = new RequestSpecBuilder()
            .setBaseUri(adminApiUrl)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilters(List.of(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
            ))
            .build();

        log.info("RestAssured request specification initialized.");
    }
}
