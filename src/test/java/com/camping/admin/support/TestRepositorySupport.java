package com.camping.admin.support;

import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.CustomerRepository;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestRepositorySupport {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public ProductRepository product() {
        return productRepository;
    }

    public ReservationRepository reservation() {
        return reservationRepository;
    }

    public CampsiteRepository campsite() {
        return campsiteRepository;
    }

    public CustomerRepository customer() {
        return customerRepository;
    }
}