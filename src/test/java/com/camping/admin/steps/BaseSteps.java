package com.camping.admin.steps;

import com.camping.admin.CampingAdminServiceApplication;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = CampingAdminServiceApplication.class
)
public class BaseSteps { }
