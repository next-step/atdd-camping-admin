package com.camping.admin.steps.support;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TestDataFactory {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Value("${admin.username}")
    private String adminUsername;

    public String createAdminToken() {
        return jwtService.generateToken(adminUsername);
    }

    public Long createProduct(String name, int stockQuantity, BigDecimal price, ProductType productType) {
        return productRepository.save(new Product(name, stockQuantity, price, productType)).getId();
    }

    public Long createCampsite(String siteNumber, String description, int maxPeople) {
        return campsiteRepository.save(new Campsite(siteNumber, description, maxPeople)).getId();
    }

    public Long createReservation(String customerName, String status) {
        String siteNumber = "A" + System.nanoTime();
        Long campsiteId = createCampsite(siteNumber, "Test Site", 4);
        Campsite campsite = campsiteRepository.findById(campsiteId).orElseThrow();
        Reservation reservation = new Reservation(customerName, LocalDate.now(), LocalDate.now().plusDays(1), campsite);
        reservation.changeStatus(status);
        return reservationRepository.save(reservation).getId();
    }
}