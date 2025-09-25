package com.camping.admin.context;

import com.camping.admin.hooks.AuthHooks;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

public class SharedContext {
    @Getter
    private static final String accessToken = AuthHooks.getAccessToken();
    @Getter @Setter
    private static ExtractableResponse<Response> response;


}
