package com.camping.admin.dto;

import com.camping.admin.domain.entity.Campsite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampsiteRequest {

    private String siteNumber;
    private String description;
    private Integer maxPeople;

    public Campsite toEntity() {
        if (siteNumber == null || siteNumber.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사이트 번호는 필수입니다.");
        }

        Integer maxPeopleValue = validMaxPeople();
        String descriptionValue = description != null ? description : "";
        return new Campsite(siteNumber, descriptionValue, maxPeopleValue);
    }

    public Integer validMaxPeople() {
        if (maxPeople == null) {
            return 1;
        }

        if (maxPeople < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최대 수용 인원은 1 이상이어야 합니다.");
        }

        if (maxPeople > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최대 수용 인원은 30 이하여야 합니다.");
        }

        return maxPeople;
    }
}