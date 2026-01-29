package com.camping.admin.support;

import com.camping.admin.api.AuthAPI;
import com.camping.admin.api.RentalAPI;
import com.camping.admin.api.ReservationAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestApiSupport {

    @Autowired
    private AuthAPI authAPI;

    @Autowired
    private RentalAPI rentalAPI;

    @Autowired
    private ReservationAPI reservationAPI;

    @Autowired
    private CustomerAPI customerAPI;

    public AuthAPI auth() {
        return authAPI;
    }

    public RentalAPI rental() {
        return rentalAPI;
    }

    public ReservationAPI reservation() {
        return reservationAPI;
    }

    public CustomerAPI customer() {
        return customerAPI;
    }
}
