package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.Reservation;
import io.cucumber.spring.ScenarioScope;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@Component
@ScenarioScope
public class TestContext {
    private String authToken;
    private ExtractableResponse<Response> response;

    private Map<String, Campsite> campsites = new HashMap<>();
    private Map<String, Product> products = new HashMap<>();
    private Map<String, Reservation> reservations = new HashMap<>();

    public void addCampsite(String siteNumber, Campsite campsite) {
        if(siteNumber != null && campsite != null) {
            campsites.put(siteNumber, campsite);
        }
    }

    public Campsite getCampsite(String siteNumber) {
        Campsite result = campsites.get(siteNumber);
        if (result == null) {
            throw new IllegalStateException("Campsite not found for siteNumber: " + siteNumber);
        }

        return result;
    }

    public void setCampsite(Campsite campsite) {
        if (campsite != null && campsite.getSiteNumber() != null) {
            this.campsites.put(campsite.getSiteNumber(), campsite);
        }
    }

    public void addProduct(String name, Product product) {
        products.put(name, product);
    }

    public Product getProduct(String name) {
        Product result = products.get(name);
        if (result == null) {
            throw new IllegalStateException("Product not found: " + name);
        }

        return result;
    }

    public void addReservation(String customerName, Reservation reservation) {
        reservations.put(customerName, reservation);
    }

    public Reservation getReservation(String customerName) {
        Reservation result = reservations.get(customerName);
        if (result == null) {
            throw new IllegalStateException("Reservation not found for customer: " + customerName);
        }

        return result;
    }
}