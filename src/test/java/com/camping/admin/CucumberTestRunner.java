package com.camping.admin;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "com.camping.admin.steps")
public class CucumberTestRunner {}
