package com.camping.admin.dto;

import com.camping.admin.domain.enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpdateReservationStatusRequest {
    
    private final ReservationStatus status;
    
    @JsonCreator
    public UpdateReservationStatusRequest(@JsonProperty("status") String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("상태값은 필수입니다.");
        }
        
        try {
            this.status = ReservationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 예약 상태입니다: " + status + 
                ". 가능한 값: WAITING, PENDING, CONFIRMED, REJECTED, CHECKED_IN, CHECKED_OUT, CANCELLED");
        }
    }
}
