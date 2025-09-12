package com.camping.admin.dto;

import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.domain.enums.ReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationDto {
    private Long id;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate reservationDate;
    private String phoneNumber;
    private ReservationStatus status;
    private String confirmationCode;
    private LocalDateTime createdAt;

    public ReservationDto(Reservation reservation) {
        this.id = reservation.getId();
        this.customerName = reservation.getCustomerName();
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.reservationDate = reservation.getReservationDate();
        this.phoneNumber = reservation.getPhoneNumber();
        this.status = reservation.getStatus();
        this.confirmationCode = reservation.getConfirmationCode();
        this.createdAt = reservation.getCreatedAt();
    }
}
