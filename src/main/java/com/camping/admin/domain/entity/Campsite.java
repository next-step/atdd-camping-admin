package com.camping.admin.domain.entity;

import com.camping.admin.exception.ValidationException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "campsites")
@Getter
@Setter
@NoArgsConstructor
public class Campsite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String siteNumber;

    private String description;

    private Integer maxPeople;

    @OneToMany(mappedBy = "campsite", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    public Campsite(String siteNumber, String description, Integer maxPeople) {
        this.siteNumber = siteNumber;
        this.description = description;
        this.maxPeople = maxPeople;
    }

    public static Campsite from(Map<String, Object> body) {
        String siteNumber = extractSiteNumber(body);
        String description = extractDescription(body);
        Integer maxPeople = extractMaxPeople(body);
        validateMaxPeople(maxPeople);
        return new Campsite(siteNumber, description, maxPeople);
    }

    private static String extractSiteNumber(Map<String, Object> body) {
        if (body.containsKey("siteNumber")) {
            Object value = body.get("siteNumber");
            if (value == null) {
                throw new ValidationException("Site number cannot be null");
            }
            return value.toString();
        }
        throw new ValidationException("Site number is required");
    }

    private static String extractDescription(Map<String, Object> body) {
        if (body.containsKey("description")) {
            Object value = body.get("description");
            return value == null ? "" : value.toString();
        }
        return "";
    }

    private static Integer extractMaxPeople(Map<String, Object> body) {
        if (body.containsKey("maxPeople")) {
            Object value = body.get("maxPeople");
            return parseMaxPeopleValue(value);
        }
        throw new ValidationException("Max people is required");
    }

    private static Integer parseMaxPeopleValue(Object value) {
        if (value == null) {
            throw new ValidationException("Max people value cannot be null");
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            return parseStringToIntegerOrNull(value.toString());
        }
    }

    private static Integer parseStringToIntegerOrNull(String stringValue) {
        try {
            return Integer.valueOf(stringValue);
        } catch (Exception e) {
            throw new ValidationException("Invalid max people format: " + stringValue);
        }
    }

    private static void validateSiteNumber(String siteNumber) {
        if (siteNumber == null || siteNumber.trim().isEmpty()) {
            throw new ValidationException("Site number is required");
        }
    }

    private static void validateMaxPeople(Integer maxPeople) {
        if (maxPeople != null && maxPeople < 0) {
            throw new ValidationException("Max people cannot be negative");
        }
    }

    public void updateFromMap(Map<String, Object> body) {
        if (body == null || body.isEmpty()) {
            return;
        }
        updateSiteNumber(body);
        updateDescription(body);
        updateMaxPeople(body);
    }

    private void updateSiteNumber(Map<String, Object> body) {
        if (body.containsKey("siteNumber")) {
            Object value = body.get("siteNumber");
            if (value != null) {
                this.siteNumber = value.toString();
            }
        }
    }

    private void updateDescription(Map<String, Object> body) {
        if (body.containsKey("description")) {
            Object value = body.get("description");
            this.description = value == null ? "" : value.toString();
        }
    }

    private void updateMaxPeople(Map<String, Object> body) {
        if (body.containsKey("maxPeople")) {
            Object value = body.get("maxPeople");
            updateMaxPeopleFromValue(value);
        }
    }

    private void updateMaxPeopleFromValue(Object value) {
        if (value instanceof Number) {
            this.maxPeople = ((Number) value).intValue();
        } else if (value != null) {
            updateMaxPeopleFromString(value.toString());
        }
    }

    private void updateMaxPeopleFromString(String stringValue) {
        try {
            this.maxPeople = Integer.valueOf(stringValue);
        } catch (Exception e) {
            throw new ValidationException("Invalid max people format during update: " + stringValue);
        }
    }
}
