package com.camping.admin.support;

import com.camping.admin.domain.entity.Reservation;
import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ScenarioContext {

    private Reservation reservation;
    private Long reservationId;
    private Response response;
    private String originalStatus;
    private String requestedStatus;

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        if (reservation != null) {
            this.reservationId = reservation.getId();
            this.originalStatus = reservation.getStatus();
        }
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getOriginalStatus() {
        return originalStatus;
    }

    public String getRequestedStatus() {
        return requestedStatus;
    }

    public void setRequestedStatus(String requestedStatus) {
        this.requestedStatus = requestedStatus;
    }

    public void clear() {
        this.reservation = null;
        this.reservationId = null;
        this.response = null;
        this.originalStatus = null;
        this.requestedStatus = null;
    }
}