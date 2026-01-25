package com.camping.admin.support;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features") //src/test/resources/features
@ConfigurationParameter(key=GLUE_PROPERTY_NAME, value = "com.camping.admin.steps") //step 정의 클래스들이 있는 패키기
@ConfigurationParameter(key=PLUGIN_PROPERTY_NAME, value = "pretty, summary") // 테스트 결과를 보기 좋게 출력
public class CucumberTestRunner {

}
