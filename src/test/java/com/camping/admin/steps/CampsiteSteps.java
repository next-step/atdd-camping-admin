package com.camping.admin.steps;

import com.camping.admin.api.CampsiteApiClient;
import com.camping.admin.context.CampsiteContext;
import com.camping.admin.context.SharedContext;
import com.camping.admin.domain.entity.Campsite;
import io.cucumber.java.en.Given;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CampsiteSteps extends BaseSteps {
    private final CampsiteContext campsiteContext = SharedContext.getCampsiteContext();

    @Given("관리자가 캠핑장을 생성한다")
    public void createCampsite() {
        ExtractableResponse<Response> response = CampsiteApiClient.sendCreateCampsite(
                SharedContext.getAccessToken(),
                Map.of(
                        "siteNumber", "A-99",
                        "description", "A-99 site description",
                        "maxPeople", 10
                )
        );
        assertThat(response.statusCode()).isEqualTo(201);
        Campsite campsite = response.jsonPath().getObject("$", Campsite.class);
        campsiteContext.setCampsite(campsite);
    }
}
