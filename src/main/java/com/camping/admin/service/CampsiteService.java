package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteRequest;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.repository.CampsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    public List<CampsiteResponse> getAllCampsites() {
        return campsiteRepository.findAll().stream()
                .map(CampsiteResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CampsiteResponse createCampsite(CampsiteRequest request) {
        Campsite campsite = new Campsite(
                request.getSiteNumber(),
                request.getDescription(),
                request.getMaxPeople()
        );
        Campsite saved = campsiteRepository.save(campsite);
        return CampsiteResponse.from(saved);
    }

    @Transactional
    public CampsiteResponse updateCampsite(Long campsiteId, CampsiteRequest request) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));

        campsite.updateCampsite(request.getSiteNumber(), request.getDescription(), request.getMaxPeople());

        return CampsiteResponse.from(campsite);
    }
}
