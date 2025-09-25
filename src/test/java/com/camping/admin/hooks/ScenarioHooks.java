package com.camping.admin.hooks;

import com.camping.admin.DatabaseCleaner;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;

public class ScenarioHooks {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Before
    public void setUp() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
        databaseCleaner.clean();
    }
}
