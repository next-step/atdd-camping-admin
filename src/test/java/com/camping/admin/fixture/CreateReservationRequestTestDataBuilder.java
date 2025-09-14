package com.camping.admin.fixture;

import com.camping.admin.dto.CreateReservationRequest;

import java.time.LocalDate;

public class CreateReservationRequestTestDataBuilder {

    // 기본값
    private String customerName = "홍길동";
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.now().plusDays(1);
    private Long campsiteId = 1L;
    private String phoneNumber = "010-0000-0000";
    private LocalDate reservationDate = LocalDate.now();

    public CreateReservationRequestTestDataBuilder withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public CreateReservationRequestTestDataBuilder withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public CreateReservationRequestTestDataBuilder withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public CreateReservationRequestTestDataBuilder withCampsiteId(Long campsiteId) {
        this.campsiteId = campsiteId;
        return this;
    }

    public CreateReservationRequestTestDataBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public CreateReservationRequestTestDataBuilder withReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
        return this;
    }

    public CreateReservationRequest build() {
        return new CreateReservationRequest(
            customerName,
            startDate,
            endDate,
            campsiteId,
            phoneNumber,
            reservationDate
        );
    }
}