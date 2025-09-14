package com.camping.admin.steps;

import com.camping.admin.CommonContext;
import com.camping.admin.client.ReservationClient;
import com.camping.admin.dto.CreateReservationRequest;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.fixture.CreateReservationRequestTestDataBuilder;
import io.cucumber.java.en.Given;

public class UpdateReservationStatusCommonSteps {
    private final ReservationClient reservationClient = new ReservationClient();

    @Given("사용자가 예약을 했다")
    public void 사용자가예약을했다() {
        CreateReservationRequest createReservationRequest = new CreateReservationRequestTestDataBuilder().build();
        ReservationResponse createdReservation = reservationClient.createReservation(createReservationRequest);
        CommonContext.reservationId = createdReservation.getId();
    }
}
