package com.camping.admin.domain.entity;

import com.camping.admin.domain.vo.SiteNumber;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "campsites")
@Getter
@Setter
@NoArgsConstructor
public class Campsite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "site_number", unique = true, nullable = false))
    private SiteNumber siteNumber;

    private String description;

    private Integer maxPeople;

    public Campsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = new SiteNumber(siteNumber);
        this.description = description == null ? "" : description;
        this.maxPeople = maxPeople;
    }

    // 기존 호환을 위한 위임 메서드
    public String getSiteNumber() {
        return siteNumber != null ? siteNumber.getValue() : null;
    }

    public void update(String siteNumber, String description, Integer maxPeople) {
        if (siteNumber != null) {
            this.siteNumber = new SiteNumber(siteNumber);
        }
        if (description != null) {
            this.description = description;
        }
        if (maxPeople != null) {
            this.maxPeople = maxPeople;
        }
    }
}