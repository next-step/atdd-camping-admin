package com.camping.admin.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationCreateRequest {

    @NotNull(message = "캠프사이트 ID는 필수입니다")
    private Long campsiteId;

    @NotBlank(message = "고객 이름은 필수입니다")
    private String customerName;

    @NotBlank(message = "전화번호는 필수입니다")
    private String customerPhone;

    @NotNull(message = "체크인 날짜는 필수입니다")
    private LocalDate checkInDate;

    @NotNull(message = "체크아웃 날짜는 필수입니다")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "인원수는 1명 이상이어야 합니다")
    private int numberOfPeople;
}
