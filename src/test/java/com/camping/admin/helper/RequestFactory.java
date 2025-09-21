package com.camping.admin.helper;

import java.util.HashMap;

/**
 * api 호출용 request 모음
 */
public class RequestFactory {

    public static HashMap<String, Object> createRentalRequest(Long reservationId,
                                                              Long productId,
                                                              Long quantity) {
        var request = new HashMap<String, Object>();
        request.put("reservationId", reservationId);
        request.put("productId", productId);
        request.put("quantity", quantity);
        return request;
    }
}
