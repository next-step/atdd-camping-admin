package com.camping.admin.helper.factory;

import java.util.HashMap;

/**
 * 대여(rental) api 호출용 request 모음
 */
public class RentalRequestFactory {

    public static HashMap<String, Object> createRentalRequest(Long reservationId,
                                                              Long productId,
                                                              Integer quantity) {
        var request = new HashMap<String, Object>();
        request.put("reservationId", reservationId);
        request.put("productId", productId);
        request.put("quantity", quantity);
        return request;
    }
}
