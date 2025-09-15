package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.dto.CampsiteUpdateRequest;
import com.camping.admin.exception.DuplicateSiteNumberException;
import com.camping.admin.repository.CampsiteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;


    @Transactional
    public Long createCampsite(CampsiteCreateRequest request) {
        String siteNumber = request.getSiteNumber();

        checkSiteExists(siteNumber);

        Campsite newCampsite = new Campsite(siteNumber, request.getDescription(), request.getMaxPeople());
        campsiteRepository.save(newCampsite);

        return newCampsite.getId();
    }

    private void checkSiteExists(String siteNumber) {
        if (campsiteRepository.existsBySiteNumber(siteNumber)) {
            throw new DuplicateSiteNumberException(siteNumber);
        }
    }

    @Transactional
    public CampsiteResponse updateCampsite(Long campsiteId, CampsiteUpdateRequest request) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));

        if (campsite.isNotSameSiteNumber(request.getSiteNumber())) {
            checkSiteExists(request.getSiteNumber());
        }

        campsite.updateSite(request.getSiteNumber(), request.getDescription(), request.getMaxPeople());

        return CampsiteResponse.from(campsite);
    }

    public List<CampsiteResponse> findAll() {
        return campsiteRepository.findAll().stream()
                .map(CampsiteResponse::from)
                .toList();
    }

    public CampsiteResponse getById(Long campsiteId) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));

        return CampsiteResponse.from(campsite);
    }

}
