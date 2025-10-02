package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.CampsiteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campsites")
@Getter
@NoArgsConstructor
public class Campsite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String siteNumber;

    private String description;

    private Integer maxPeople;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampsiteStatus status = CampsiteStatus.AVAILABLE;

    @OneToMany(mappedBy = "campsite", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    public Campsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
        this.status = CampsiteStatus.AVAILABLE;
    }

    public void updateCampsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }

    public void updateStatus(CampsiteStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Campsite status cannot be null");
        }
        this.status = status;
    }

    public boolean isAvailable() {
        return this.status == CampsiteStatus.AVAILABLE;
    }

    public boolean isAvailableForReservation(LocalDate startDate, LocalDate endDate) {
        if (!isAvailable()) {
            return false;
        }

        return reservations.stream()
                .filter(r -> !r.getStatus().equals(com.camping.admin.domain.value.ReservationStatus.CANCELLED))
                .noneMatch(r -> isOverlapping(r.getStartDate(), r.getEndDate(), startDate, endDate));
    }

    private boolean isOverlapping(LocalDate existingStart, LocalDate existingEnd,
                                   LocalDate newStart, LocalDate newEnd) {
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }

    public void validateAvailability() {
        if (!isAvailable()) {
            throw new IllegalStateException("Campsite " + siteNumber + " is not available. Current status: " + status);
        }
    }
}
