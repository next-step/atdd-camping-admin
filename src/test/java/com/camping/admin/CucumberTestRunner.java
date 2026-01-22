package com.camping.admin;


import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.camping.admin.steps") // Step 정의 클래스들이 있는 패키지를 지정합니다.
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary") // 테스트 결과를 보기 좋게 출력합니다.
public class CucumberTestRunner {
}
