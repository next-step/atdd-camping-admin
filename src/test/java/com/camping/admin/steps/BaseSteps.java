package com.camping.admin.steps;

import com.camping.admin.CampingAdminServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = CampingAdminServiceApplication.class
)
public class BaseSteps { }
