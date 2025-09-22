package com.camping.admin.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "com.camping.admin")
public class CucumberTestRunner {

}
