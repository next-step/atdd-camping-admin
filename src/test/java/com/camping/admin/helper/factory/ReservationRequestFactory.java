package com.camping.admin.helper.factory;

import java.util.HashMap;

/**
 * 예약(reservation) api 호출용 request 모음
 */
public class ReservationRequestFactory {

    public static HashMap<String, Object> updateRentalRequest(String status) {
        var request = new HashMap<String, Object>();
        request.put("status", status);
        return request;
    }
}
