package com.camping.admin.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public AdminCredentials adminCredentials(
            @Value("${admin.username}") String username,
            @Value("${admin.password}") String password) {
        return new AdminCredentials(username, password);
    }
}
