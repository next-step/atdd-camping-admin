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

    public List<Campsite> getAllCampsitesForWeb() {
        return campsiteRepository.findAll();
    }

    @Transactional
    public Campsite createCampsite(Map<String, Object> body) {
        validateCampsiteData(body);
        
        String siteNumber = extractSiteNumber(body);
        String description = extractDescription(body);
        Integer maxPeople = extractMaxPeople(body);
        
        return createAndSaveCampsite(siteNumber, description, maxPeople);
    }

    private void validateCampsiteData(Map<String, Object> body) {
        String siteNumber = extractSiteNumber(body);
        validateSiteNumber(siteNumber);
        validateMaxPeople(extractMaxPeople(body));
    }

    private void validateSiteNumber(String siteNumber) {
        if (siteNumber == null || siteNumber.trim().isEmpty()) {
            throw new ValidationException("Site number is required");
        }
    }

    private void validateMaxPeople(Integer maxPeople) {
        if (maxPeople != null && maxPeople < 0) {
            throw new ValidationException("Max people cannot be negative");
        }
    }

    private Campsite createAndSaveCampsite(String siteNumber, String description, Integer maxPeople) {
        try {
            Campsite newCampsite = new Campsite(siteNumber, description, maxPeople);
            return campsiteRepository.save(newCampsite);
        } catch (DataIntegrityViolationException e) {
            throw new CampsiteConflictException("Site number already exists");
        }
    }

    @Transactional
    public Campsite updateCampsite(Long campsiteId, Map<String, Object> body) {
        Campsite campsite = findById(campsiteId);
        updateCampsiteFields(campsite, body);
        return campsite;
    }

    private Campsite findById(Long campsiteId) {
        return campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find campsite with id: " + campsiteId));
    }

    private void updateCampsiteFields(Campsite campsite, Map<String, Object> body) {
        if (body != null && !body.isEmpty()) {
            updateSiteNumber(campsite, body);
            updateDescription(campsite, body);
            updateMaxPeople(campsite, body);
        }
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

    @Transactional
    public Campsite createCampsiteFromWebForm(Map<String, String> params) {
        String siteNumber = extractSiteNumberFromForm(params);
        String description = extractDescriptionFromForm(params);
        Integer maxPeople = extractMaxPeopleFromForm(params);

        return createCampsiteEntity(siteNumber, description, maxPeople);
    }

    private Campsite createCampsiteEntity(String siteNumber, String description, Integer maxPeople) {
        Campsite entity = new Campsite(siteNumber, description, maxPeople);
        return campsiteRepository.save(entity);
    }

    public Campsite findCampsiteById(Long id) {
        return campsiteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find campsite with id: " + id));
    }

    @Transactional
    public Campsite updateCampsiteFromWebForm(Long id, Map<String, String> params) {
        Campsite campsite = findCampsiteById(id);
        updateCampsiteFromForm(campsite, params);
        return campsite;
    }

    private String extractSiteNumberFromForm(Map<String, String> params) {
        return params.containsKey("siteNumber") ? params.get("siteNumber") : null;
    }

    private String extractDescriptionFromForm(Map<String, String> params) {
        return params.containsKey("description") ? params.get("description") : "";
    }

    private Integer extractMaxPeopleFromForm(Map<String, String> params) {
        if (params.containsKey("maxPeople")) {
            try {
                return Integer.valueOf(params.get("maxPeople"));
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void updateCampsiteFromForm(Campsite campsite, Map<String, String> params) {
        if (params.containsKey("siteNumber")) {
            campsite.setSiteNumber(params.get("siteNumber"));
        }
        if (params.containsKey("description")) {
            campsite.setDescription(params.get("description"));
        }
        if (params.containsKey("maxPeople")) {
            try {
                campsite.setMaxPeople(Integer.valueOf(params.get("maxPeople")));
            } catch (Exception ignore) {
            }
        }
    }
}
