package com.camping.admin.controller.dto;

import com.camping.admin.service.dto.CampsiteCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampsiteRequest {

    @NotBlank(message = "사이트 번호는 필수입니다.")
    private String siteNumber;

    private String description;

    private Integer maxPeople;

    public CampsiteCommand toCommand() {
        return new CampsiteCommand(siteNumber, description, maxPeople);
    }
}
