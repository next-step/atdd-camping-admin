package com.camping.admin.support;

import io.restassured.response.Response;

public class SharedState {
    private Response response;
    private Long productId;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
