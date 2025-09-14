package com.camping.admin.dto;

import lombok.*;

import java.time.LocalDate;

import static com.camping.admin.utils.ValidatorUtils.isNullOrBlank;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateReservationRequest {
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long campsiteId;
    private String phoneNumber;
    private LocalDate reservationDate;

    public void validate() {
        if(isNullOrBlank(customerName)) {;
            throw new RuntimeException("customerName is required");
        }
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new RuntimeException("Invalid dates");
        }

        if (campsiteId == null || campsiteId <= 0) {
            throw new RuntimeException("Campsite ID is required and must be positive");
        }
    }
}
