package com.camping.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CreateReservationRequest {
    private Long campsiteId;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String phoneNumber;

    public CreateReservationRequest(Long campsiteId, String customerName, LocalDate startDate, LocalDate endDate) {
        this.campsiteId = campsiteId;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
