package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Setter
@NoArgsConstructor
public class CreateCampsiteRequest {

    private String siteNumber;
    private String description;
    private Integer maxPeople;

    public CreateCampsiteRequest(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }

    public Campsite toEntity() {
        if (siteNumber == null || siteNumber.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사이트 번호는 필수입니다.");
        }

        Integer maxPeopleValue = validMaxPeople();
        String descriptionValue = description != null ? description : "";
        return new Campsite(siteNumber, descriptionValue, maxPeopleValue);
    }

    public Integer validMaxPeople() {
        if (maxPeople != null && maxPeople < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최대 수용 인원은 0 이상이어야 합니다.");
        }

        return maxPeople;
    }
}