package com.camping.admin.steps;

import com.camping.admin.CampingAdminServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = CampingAdminServiceApplication.class
)
public class BaseSteps {
    @BeforeEach
    public void setUp() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
    }
}
