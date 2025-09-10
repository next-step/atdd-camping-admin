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
        String siteNumber = Campsite.extractSiteNumber(body);
        String description = Campsite.extractDescription(body);
        Integer maxPeople = Campsite.extractMaxPeople(body);

        Campsite.validateMaxPeople(maxPeople);

        return createAndSaveCampsite(siteNumber, description, maxPeople);
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
        campsite.updateFromMap(body);
        return campsite;
    }

    private Campsite findById(Long campsiteId) {
        return campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find campsite with id: " + campsiteId));
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
        if (params.containsKey("siteNumber")) {
            String siteNumber = params.get("siteNumber");
            if (siteNumber == null || siteNumber.trim().isEmpty()) {
                throw new ValidationException("Site number cannot be empty in form");
            }
            return siteNumber;
        }
        throw new ValidationException("Site number is required in form");
    }

    private String extractDescriptionFromForm(Map<String, String> params) {
        return params.containsKey("description") ? params.get("description") : "";
    }

    private Integer extractMaxPeopleFromForm(Map<String, String> params) {
        if (params.containsKey("maxPeople")) {
            return parseMaxPeopleFromFormValue(params.get("maxPeople"));
        } else {
            throw new ValidationException("Max people is required in form");
        }
    }

    private Integer parseMaxPeopleFromFormValue(String maxPeopleValue) {
        try {
            return Integer.valueOf(maxPeopleValue);
        } catch (Exception e) {
            throw new ValidationException("Invalid max people format in form: " + maxPeopleValue);
        }
    }

    private void updateCampsiteFromForm(Campsite campsite, Map<String, String> params) {
        updateSiteNumberFromForm(campsite, params);
        updateDescriptionFromForm(campsite, params);
        updateMaxPeopleFromForm(campsite, params);
    }

    private void updateSiteNumberFromForm(Campsite campsite, Map<String, String> params) {
        if (params.containsKey("siteNumber")) {
            campsite.setSiteNumber(params.get("siteNumber"));
        }
    }

    private void updateDescriptionFromForm(Campsite campsite, Map<String, String> params) {
        if (params.containsKey("description")) {
            campsite.setDescription(params.get("description"));
        }
    }

    private void updateMaxPeopleFromForm(Campsite campsite, Map<String, String> params) {
        if (params.containsKey("maxPeople")) {
            try {
                campsite.setMaxPeople(Integer.valueOf(params.get("maxPeople")));
            } catch (Exception e) {
                throw new ValidationException("Invalid max people format during form update: " + params.get("maxPeople"));
            }
        }
    }
}
