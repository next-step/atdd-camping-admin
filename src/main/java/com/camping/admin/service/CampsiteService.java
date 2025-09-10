package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteDto;
import com.camping.admin.exception.CampsiteConflictException;
import com.camping.admin.exception.EntityNotFoundException;
import com.camping.admin.exception.ValidationException;
import com.camping.admin.repository.CampsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    public List<CampsiteDto> getAllCampsites() {
        List<Campsite> all = campsiteRepository.findAll();
        return all.stream()
                .map(CampsiteDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Campsite createCampsite(Map<String, Object> body) {
        String siteNumber = extractSiteNumber(body);
        if (siteNumber == null || siteNumber.trim().isEmpty()) {
            throw new ValidationException("Site number is required");
        }

        String description = extractDescription(body);
        Integer maxPeople = extractMaxPeople(body);

        if (maxPeople != null && maxPeople < 0) {
            throw new ValidationException("Max people cannot be negative");
        }

        try {
            Campsite newCampsite = new Campsite(siteNumber, description, maxPeople);
            return campsiteRepository.save(newCampsite);
        } catch (DataIntegrityViolationException e) {
            throw new CampsiteConflictException("Site number already exists");
        }
    }

    @Transactional
    public Campsite updateCampsite(Long campsiteId, Map<String, Object> body) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find campsite with id: " + campsiteId));

        if (body != null && !body.isEmpty()) {
            updateSiteNumber(campsite, body);
            updateDescription(campsite, body);
            updateMaxPeople(campsite, body);
        }

        return campsite;
    }

    private String extractSiteNumber(Map<String, Object> body) {
        if (body.containsKey("siteNumber")) {
            Object v = body.get("siteNumber");
            if (v == null) {
                return null;
            } else {
                return v.toString();
            }
        } else {
            return null;
        }
    }

    private String extractDescription(Map<String, Object> body) {
        if (body.containsKey("description")) {
            Object d = body.get("description");
            return d == null ? "" : d.toString();
        } else {
            return "";
        }
    }

    private Integer extractMaxPeople(Map<String, Object> body) {
        if (body.containsKey("maxPeople")) {
            Object m = body.get("maxPeople");
            if (m == null) {
                return null;
            } else if (m instanceof Number) {
                return ((Number) m).intValue();
            } else {
                try {
                    return Integer.valueOf(m.toString());
                } catch (Exception e) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private void updateSiteNumber(Campsite campsite, Map<String, Object> body) {
        if (body.containsKey("siteNumber")) {
            Object v = body.get("siteNumber");
            if (v != null) {
                campsite.setSiteNumber(v.toString());
            }
        }
    }

    private void updateDescription(Campsite campsite, Map<String, Object> body) {
        if (body.containsKey("description")) {
            Object v = body.get("description");
            if (v == null) {
                campsite.setDescription("");
            } else {
                campsite.setDescription(v.toString());
            }
        }
    }

    private void updateMaxPeople(Campsite campsite, Map<String, Object> body) {
        if (body.containsKey("maxPeople")) {
            Object v = body.get("maxPeople");
            if (v instanceof Number) {
                campsite.setMaxPeople(((Number) v).intValue());
            } else if (v != null) {
                try {
                    campsite.setMaxPeople(Integer.valueOf(v.toString()));
                } catch (Exception ignore) {
                }
            }
        }
    }
}
