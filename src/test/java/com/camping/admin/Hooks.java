package com.camping.admin;

import com.camping.admin.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {
    @Before
    public void beforeScenario() {
        ScenarioContext.clear();
    }

    @After
    public void afterScenario() {
        ScenarioContext.clear();
    }
}
