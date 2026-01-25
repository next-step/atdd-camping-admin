package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import io.cucumber.spring.ScenarioScope;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
public class TestContext {
    private String authToken;
    private Campsite campsite;
    private Long reservationId;
    private ExtractableResponse<Response> response;
}
