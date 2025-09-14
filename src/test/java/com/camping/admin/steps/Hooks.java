package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import com.camping.admin.support.RequestSpecFactory;
import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void beforeScenario() { CommonContext.requestSpec = RequestSpecFactory.create(); }

}
