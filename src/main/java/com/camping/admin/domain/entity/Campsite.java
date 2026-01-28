package com.camping.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campsites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Campsite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String siteNumber;

    private String description;

    private Integer maxPeople;

    @JsonIgnore
    @OneToMany(mappedBy = "campsite", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();
    
    public Campsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }

    public void update(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber != null ? siteNumber : this.siteNumber;
        this.description = description != null ? description : this.description;
        this.maxPeople = maxPeople != null ? maxPeople : this.maxPeople;
    }
}