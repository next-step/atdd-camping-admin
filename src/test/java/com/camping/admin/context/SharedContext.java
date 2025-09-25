package com.camping.admin.context;

import com.camping.admin.api.AuthApiClient;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

public class SharedContext {
    @Getter
    private static final String accessToken = AuthApiClient.getAccessToken();
    @Getter @Setter
    private static ExtractableResponse<Response> response;
    @Getter
    private static final CampsiteContext campsiteContext = new CampsiteContext();
    @Getter
    private static final ReservationContext reservationContext = new ReservationContext();
}
