package com.camping.admin.context;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScenarioContext {
    private ExtractableResponse<Response> response;
    private long reservationId;
}
