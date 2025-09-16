package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.repository.CampsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampsiteService {
    private final CampsiteRepository campsiteRepository;

    public List<CampsiteResponse> readAll() {
        List<Campsite> campsites = campsiteRepository.findAll();

        if (campsites.isEmpty()) {
            return new ArrayList<>();
        }
        return campsites.stream().map(CampsiteResponse::of).toList();
    }

    public CampsiteResponse create(CampsiteCreateRequest request) {
        Campsite newCampsite = new Campsite(request.siteNumber(), request.description(), request.maxPeople());
        return CampsiteResponse.of(campsiteRepository.save(newCampsite));
    }

    public CampsiteResponse update(Long campsiteId, CampsiteCreateRequest request) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException(campsiteId + " id 를 가지는 캠핑사이트를 찾을 수 없습니다."));

        campsite.setSiteNumber(request.siteNumber());
        campsite.setDescription(request.description());
        campsite.setMaxPeople(request.maxPeople());

        return CampsiteResponse.of(campsiteRepository.save(campsite));
    }
}
