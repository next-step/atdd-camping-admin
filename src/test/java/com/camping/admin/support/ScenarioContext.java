package com.camping.admin.support;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.dto.UpdateReservationStatusRequest;
import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ScenarioContext {

    private Reservation reservation;
    private Long reservationId;
    private Response response;
    private UpdateReservationStatusRequest updateReservationRequest;
    private Campsite campsite;

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        if (reservation != null) {
            this.reservationId = reservation.getId();
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

    public void setCampsite(Campsite campsite) {
        this.campsite = campsite;
    }

    public void setUpdateReservationRequestStatus(ReservationStatus status) {
        this.updateReservationRequest = new UpdateReservationStatusRequest(status);
    }

    public UpdateReservationStatusRequest getUpdateReservationRequest() {
        return updateReservationRequest;
    }
}